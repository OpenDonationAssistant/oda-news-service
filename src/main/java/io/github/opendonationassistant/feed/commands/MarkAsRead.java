package io.github.opendonationassistant.feed.commands;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.feed.repository.StreamerFeedRepository;
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
public class MarkAsRead extends BaseController {

  private final ODALogger log = new ODALogger(MarkAsRead.class);
  private final StreamerFeedRepository repository;

  @Inject
  public MarkAsRead(StreamerFeedRepository repository) {
    this.repository = repository;
  }

  @Post("/feed/commands/mark-as-read")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> markAsRead(
    @NonNull Authentication auth,
    @Valid @Body MarkAsReadCommand command
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    log.info(
      "Executing MarkAsReadCommand",
      Map.of("recipientId", ownerId.get(), "newsId", command.newsId())
    );
    repository.get(ownerId.get()).markAsRead(command.newsId());
    return HttpResponse.ok();
  }

  @Serdeable
  public static record MarkAsReadCommand(@NotBlank String newsId) {}
}
