# AGENTS.md

Micronaut 5 (Java 25) news/feed service for the OpenDonationAssistant platform. Maven build, PostgreSQL + Flyway + Micronaut Data JDBC, Infinispan embedded cache, JWT auth via Keycloak.

## Build & test

- No Maven wrapper — use system `mvn`. **JDK 25 is required** (`<jdk.version>` in `pom.xml`; older JDKs fail).
- `.mvn/jvm.config` supplies the `--add-exports`/`--add-opens` javac flags needed by the ErrorProne/NullAway annotation processors. Don't delete it.
- `mvn compile` — runs annotation processors; **ErrorProne and NullAway (`NullAway:ERROR`) are build-failing**, not advisory. Compilation fails on null-safety violations.
- `mvn test` — JUnit 5, spins up PostgreSQL via Micronaut Test Resources (`micronaut.test.resources.enabled=true`). **Requires Docker** (or a reachable Postgres) for the `allinone` tests.
- Single test: `mvn test -Dtest=CreateAndReadNewsTest`
- Tests use `@MicronautTest(environments = "allinone")` + Instancio `@Given` for random fixtures; `Authentication` is mocked via Mockito (`preferred_username` attribute) — no real JWT is minted.
- `mvn package` — jar + JaCoCo report. `packaging` is parameterized as `${packaging}` (default `jar`); the release pipeline switches it to produce a GraalVM native binary at `target/oda-news-service`, which the `Dockerfile` copies.

## Config & environments

- Default environment is **`standalone`** (set in `Application.Configurer`); tests override it with **`allinone`** (`@MicronautTest(environments = "allinone")`).
- `application-standalone.yml` reads `JDBC_URL`, `JDBC_USER`, `JDBC_PASSWORD` (defaults `jdbc:postgresql://localhost/postgres` / `postgres` / `postgres`).
- `JWKS_URI` is required with **no default** — the Keycloak JWKS URL for JWT signature verification.
- `Application.java` also declares an Infinispan `RemoteCacheManager` bean bound to `infinispan.client.hotrod.*` properties (no defaults in repo), but **nothing injects it** — the warning cache uses the embedded `EmbeddedCacheManager` instead, so those hotrod props aren't actually required to run.

## Architecture (non-obvious)

Each domain module (`news`, `advice`, `feed`, `feedback`, `warning`) follows the same layered layout under `io.github.opendonationassistant`:

- `{module}/Xxx.java` — domain entity, typically holding its own `DataRepository` ref and exposing `asDto()`/`save()` (`News`, `Advice`). Simpler modules deviate: `NewsFeedback` is a plain POJO (its `Data` class exposes `asNewsFeedback()`), and `StreamerFeed` exposes `nextNews()`/`markAsRead()` instead.
- `{module}/view/` — read/query `@Controller`s returning `@Serdeable` DTO records.
- `{module}/commands/` — command endpoints (`@Controller` + `@Post("/.../commands/...")`), often nested `record` command bodies marked `@Serdeable`.
- `{module}/repository/` — three files:
  - `XxxData` — `@MappedEntity` (table name in `@MappedEntity("...")`).
  - `XxxDataRepository` — `@JdbcRepository(dialect = Dialect.POSTGRES)` interface extending `CrudRepository<XxxData, String>`.
  - `XxxRepository` — `@Singleton` domain repository; generates String UUIDs via `com.fasterxml.uuid.Generators.timeBasedEpochGenerator()`.

Key patterns:
- **IDs are String UUIDs generated in the domain repository — no `@GeneratedValue`.** New entities must follow this, not auto-increment/identity columns. (Known deviation: `NewsFeedbackData` generates its own ID in its constructor.)
- **Auth identity**: `BaseController.getOwnerId(auth)` returns the `preferred_username` claim. Commands are `@Secured(SecurityRule.IS_AUTHENTICATED)`; public reads use `@Secured(SecurityRule.IS_ANONYMOUS)`. Controllers extending `BaseController` must not reimplement `getOwnerId` (some older controllers, e.g. `StreamerFeedController`/`FeedbackCommandsController`, inline it instead — prefer `BaseController`).
- **Shared libs**: `BaseController` and `ODALogger` come from `io.github.opendonationassistant:oda-commons`, pulled transitively via `oda-rabbit-conf`. The ODA libs version is pinned by `<oda.version>` (`0.11.232`).
- **Flyway** migrations live in `src/main/resources/db/migration/`, schema `news`; add new ones as sequential `V{n}__*.sql`. Tables have **no PK/FK constraints** — integrity is logical, enforced in the domain layer.
- **Logging**: use `ODALogger` with structured `Map.of("key", value, ...)` context rather than string interpolation. `logback.xml` emits JSON lines.
- **Events**: `Application.eventsFacade` exposes a `@Named("events") RabbitClient` that publishes to the `"notifications"` routing key (3rd constructor arg). Only `WarningCommandsController` uses it today, sending `AddedWarningEvent` (which implements `HasRecipientId`).

Module quirks:
- **warning has no database** — state lives in an in-memory Infinispan `VOLATILE` cache exposed as a raw `Map<String, List<WarningData>>` bean (`WarningCacheConfiguration.streamelementsCache`); warnings are lost on restart.
- **feed depends on news** — `StreamerFeed`/`StreamerFeedRepository` use `NewsRepository` to compute "next unread news"; the cursor is `feed.last_read_news_id`.
- **`GetRandomAdvice` is a GET read** but lives in `advice/commands/`; `advice/view/` holds only the `AdviceDto` record.
- **`Application.java` is both main class and `@Factory`** — infra beans (RabbitClient, both Infinispan managers) live in the entry point.
- **Dead code**: the `RemoteCacheManager` bean (nothing injects it) and an unused SLF4J `Logger` in `StreamerFeed`.

## Style & correctness

- JSpecify nullability is enforced: annotate fields/params with `org.jspecify.annotations.@NonNull`/`@Nullable`; `package-info.java` marks the root package `@NullUnmarked`. NullAway fails the build otherwise.
- `toString()` methods on domain/data classes emit hand-rolled JSON-ish strings — match that style when adding new entities.
- Version is declared in multiple places that drift: `pom.xml` (`0.7.0`) vs the hardcoded `@Info(version = "0.7.0")` in `Application.java`. Currently in sync — update both when bumping.
- OpenAPI spec is generated from `@OpenAPIDefinition`; `openapi-config.json` publishes a TypeScript client (`@opendonationassistant/oda-news-service-client`) to GitHub npm — only touch it when changing client generation.

## Testing conventions

- **Bean-invocation style**: controllers/commands are `@Inject`ed and called as Java objects — no HTTP layer, no `@TestHttpClient` (the `micronaut-http-client` dep is unused).
- No `src/test/resources/` — Test Resources provisions Postgres from `pom.xml`; `application-allinone.yml` only sets `db-type: postgresql`.
- Ordering assertions exploit `timeBasedEpochGenerator()` lexicographic sort (fabricate IDs with `Thread.sleep(10)`).
- Warning tests mutate the real embedded Infinispan cache directly; each test seeds a distinct streamer key to stay isolated.
- `awaitility` is declared but unused.

## Release

- CI (`.github/workflows/maven.yml`): pushes to `main` (ignoring `README.md`) trigger `OpenDonationAssistant/oda-libraries`' reusable `release_service.yml`, which builds/deploys with `version = ${{ github.RUN_NUMBER }}`. There are no other checks locally — rely on `mvn test` for verification before pushing.
- Tests actually run inside the Sonar step (`mvn verify ... sonar`); the later `clean package` step is `-DskipTests`.
- `Dockerfile` copies only the native binary `target/oda-news-service` — it only builds under `-Dpackaging=native-image`.
- Latent gap: `aot-${packaging}.properties` is referenced but only `aot-jar.properties` exists (native build runs without its AOT config).
