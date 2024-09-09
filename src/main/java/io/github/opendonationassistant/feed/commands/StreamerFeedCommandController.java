package io.github.opendonationassistant.feed.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.opendonationassistant.feed.repository.StreamerFeedRepository;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.annotation.Nonnull;

@Controller
public class StreamerFeedCommandController {

  private Logger log = LoggerFactory.getLogger(StreamerFeedCommandController.class);
  private final StreamerFeedRepository repository;

  public StreamerFeedCommandController(StreamerFeedRepository repository) {
    this.repository = repository;
  }

  @Post("/feed/commands/mark-as-read")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public void markAsRead(
    @Nonnull Authentication auth,
    @Body MarkAsReadCommand command
  ) {
    log.info("Mark as read: {}", command.getNewsId());
    command.executeWith(auth, repository);
  }
}
