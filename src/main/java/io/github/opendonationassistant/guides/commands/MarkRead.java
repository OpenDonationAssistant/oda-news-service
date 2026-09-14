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
import jakarta.inject.Inject;
import java.util.Map;
import org.jspecify.annotations.NonNull;

@Controller
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
    @Body MarkReadCommand command
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
  public static record MarkReadCommand(String guideId) {}
}