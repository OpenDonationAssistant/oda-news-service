package io.github.opendonationassistant.warning.commands;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.events.HasRecipientId;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.github.opendonationassistant.warning.repository.WarningData;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.Nullable;

@Controller
public class WarningCommandsController extends BaseController {

  private final ODALogger log = new ODALogger(WarningCommandsController.class);
  private final Map<String, List<WarningData>> warnings;
  private final RabbitClient eventsFacade;

  @Inject
  public WarningCommandsController(
    Map<String, List<WarningData>> warnings,
    @Named("events") RabbitClient eventsFacade
  ) {
    this.warnings = warnings;
    this.eventsFacade = eventsFacade;
  }

  @Post("/warnings/commands/clear")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public CompletableFuture<HttpResponse<Void>> clearWarnings(
    Authentication auth
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return CompletableFuture.completedFuture(HttpResponse.unauthorized());
    }
    log.info("Clearing warnings", Map.of("recipientId", ownerId.get()));
    return CompletableFuture.supplyAsync(() -> {
      warnings.remove(ownerId.get());
      return HttpResponse.ok();
    });
  }

  @Post("/warnings/commands/create")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public CompletableFuture<HttpResponse<Void>> addWarning(
    Authentication auth,
    @Body AddWarningCommand command
  ) {
    var recipientId = getOwnerId(auth);
    if (recipientId.isEmpty()) {
      return CompletableFuture.completedFuture(HttpResponse.unauthorized());
    }
    log.info(
      "Adding warning",
      Map.of("recipientId", recipientId.get(), "message", command.message())
    );
    return CompletableFuture.supplyAsync(() -> {
      var list = new ArrayList<>(
        warnings.getOrDefault(recipientId.get(), new ArrayList<>())
      );
      list.add(new WarningData(command.message(), command.component()));
      warnings.put(recipientId.get(), list);
      try {
        eventsFacade.sendEvent(
          new AddedWarningEvent(recipientId.get(), command.message())
        );
      } catch (Exception e) {
        log.error("Failed to send AddedWarningEvent", e);
      }
      return HttpResponse.ok();
    });
  }

  @Serdeable
  public static record AddedWarningEvent(String recipientId, String message)
    implements HasRecipientId {}

  @Serdeable
  public static record AddWarningCommand(
    String message,
    @Nullable String component
  ) {
    public AddWarningCommand(String message) {
      this(message, null);
    }
  }
}
