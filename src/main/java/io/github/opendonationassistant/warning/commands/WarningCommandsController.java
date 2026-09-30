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
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@Controller
@Validated
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
  public HttpResponse<Void> clearWarnings(
    Authentication auth,
    @Valid @Body ClearWarningsCommand command
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    log.info(
      "Clearing warnings",
      Map.of(
        "recipientId",
        ownerId.get(),
        "components",
        Optional.ofNullable(command.components()).orElse(List.of())
      )
    );
    var components = command.components();
    if (components == null) {
      warnings.remove(ownerId.get());
    } else {
      warnings.computeIfPresent(ownerId.get(), (key, existing) -> {
        var list = new ArrayList<>(existing);
        list.removeIf(
          warning ->
            warning.component() != null &&
            components.contains(warning.component())
        );
        return list;
      });
    }
    return HttpResponse.ok();
  }

  @Post("/warnings/commands/create")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> addWarning(
    Authentication auth,
    @Valid @Body AddWarningCommand command
  ) {
    var recipientId = getOwnerId(auth);
    if (recipientId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    log.info(
      "Adding warning",
      Map.of("recipientId", recipientId.get(), "message", command.message())
    );
    var newWarning = new WarningData(
      command.message(),
      command.component(),
      System.currentTimeMillis(),
      command.priority() == null ? "Notification" : command.priority()
    );
    var component = command.component();
    warnings.compute(recipientId.get(), (key, existing) -> {
      var list = new ArrayList<>(existing == null ? List.of() : existing);
      if (component != null) {
        for (int i = 0; i < list.size(); i++) {
          if (component.equals(list.get(i).component())) {
            list.set(i, newWarning);
            return list;
          }
        }
      }
      list.add(newWarning);
      return list;
    });
    try {
      eventsFacade.sendEvent(
        new AddedWarningEvent(recipientId.get(), command.message())
      );
    } catch (Exception e) {
      log.error("Failed to send AddedWarningEvent", e);
    }
    return HttpResponse.ok();
  }

  @Serdeable
  public static record AddedWarningEvent(String recipientId, String message)
    implements HasRecipientId {}

  @Serdeable
  public static record AddWarningCommand(
    @NotBlank String message,
    @Nullable String component,
    @Nullable String priority
  ) {
    public AddWarningCommand(String message) {
      this(message, null, null);
    }

    public AddWarningCommand(String message, @Nullable String component) {
      this(message, component, null);
    }
  }

  @Serdeable
  public static record ClearWarningsCommand(
    @Nullable List<String> components
  ) {}
}
