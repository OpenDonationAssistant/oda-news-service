package io.github.opendonationassistant.guides.commands;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.guides.repository.GuidesRepository;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.jspecify.annotations.NonNull;

@Controller
@Validated
public class MarkRead extends BaseController {

  private final ODALogger log = new ODALogger(MarkRead.class);
  private final GuidesRepository guidesRepository;

  @Inject
  public MarkRead(GuidesRepository guidesRepository) {
    this.guidesRepository = guidesRepository;
  }

  @Post("/guides/commands/mark-read")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> markRead(
    @NonNull Authentication auth,
    @Valid @Body MarkReadCommand command
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    log.info(
      "Executing MarkReadCommand",
      Map.of("recipientId", ownerId.get(), "guideId", command.guideId())
    );
    guidesRepository.get(ownerId.get()).markRead(command.guideId());
    return HttpResponse.ok();
  }

  @Serdeable
  public static record MarkReadCommand(@NotBlank String guideId) {}
}
