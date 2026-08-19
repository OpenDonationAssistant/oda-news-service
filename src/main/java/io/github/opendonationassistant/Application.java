package io.github.opendonationassistant;

import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.ApplicationContextConfigurer;
import io.micronaut.context.annotation.ContextConfigurer;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import jakarta.inject.Singleton;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

@OpenAPIDefinition(
  info = @Info(
    title = "ODA News Service",
    version = "0.5.0",
    description = "News Service API",
    license = @License(
      name = "AGPL-3.0",
      url = "https://www.gnu.org/licenses/agpl-3.0.en.html"
    ),
    contact = @Contact(name = "stCarolas", email = "stcarolas@gmail.com")
  )
)
@Factory
public class Application {

  @ContextConfigurer
  public static class Configurer implements ApplicationContextConfigurer {

    @Override
    public void configure(@NonNull ApplicationContextBuilder builder) {
      builder.defaultEnvironments("standalone");
    }
  }

  public static void main(String[] args) {
    Micronaut.build(args).banner(false).classes(Application.class).start();
  }

  @Singleton
  public RemoteCacheManager remoteCacheManager(
    @Value("${infinispan.client.hotrod.server.host}") String host,
    @Value("${infinispan.client.hotrod.server.port}") int port,
    @Value(
      "${infinispan.client.hotrod.security.authentication.username}"
    ) String username,
    @Value(
      "${infinispan.client.hotrod.security.authentication.password}"
    ) String password
  ) {
    var conf = new ConfigurationBuilder()
      .addServer()
      .host(host)
      .port(port)
      .security()
      .authentication()
      .username(username)
      .password(password)
      .build();
    var manager = new RemoteCacheManager(conf);
    manager.start();
    return manager;
  }

  @Singleton
  public EmbeddedCacheManager embeddedCacheManager() {
    // prettier-ignore ON
    var configuration = new GlobalConfigurationBuilder()
      .nonClusteredDefault()
      .build();
    // prettier-ignore OFF
    var manager = new DefaultCacheManager(configuration);
    manager.start();
    return manager;
  }
}
