package io.github.opendonationassistant.warning.view;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.warning.repository.WarningData;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
public class WarningController extends BaseController {

  private ODALogger log = new ODALogger(this);

  private final Map<String, List<WarningData>> warnings;

  public WarningController(Map<String, List<WarningData>> warnings) {
    this.warnings = warnings;
  }

  @Get("/warnings")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public @Nonnull CompletableFuture<
    HttpResponse<List<WarningData>>
  > getWarnings(@Nonnull Authentication auth) {
    var recipientId = getOwnerId(auth);
    if (recipientId.isEmpty()) {
      return CompletableFuture.completedFuture(HttpResponse.unauthorized());
    }
    log.debug("Getting warnings", Map.of("recipientId", recipientId));
    return CompletableFuture.supplyAsync(() -> {
      var now = System.currentTimeMillis();
      var all = warnings.getOrDefault(recipientId.get(), List.of());
      var filtered = all
        .stream()
        .filter(w -> now - w.timestamp() > 180_000)
        .toList();
      return HttpResponse.ok(filtered);
    });
  }
}
