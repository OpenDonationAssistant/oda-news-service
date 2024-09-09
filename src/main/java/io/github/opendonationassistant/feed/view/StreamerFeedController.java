package io.github.opendonationassistant.feed.view;

import io.github.opendonationassistant.feed.StreamerFeed;
import io.github.opendonationassistant.feed.repository.StreamerFeedRepository;
import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.view.NewsDto;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.annotation.Nonnull;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class StreamerFeedController {

  private Logger log = LoggerFactory.getLogger(StreamerFeedController.class);

  private final StreamerFeedRepository streamerFeedRepository;

  public StreamerFeedController(StreamerFeedRepository streamerFeedRepository) {
    this.streamerFeedRepository = streamerFeedRepository;
  }

  @Get("/feed/news")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public List<NewsDto> getFeed(@Nonnull Authentication auth) {
    String streamerId = getOwnerId(auth);
    log.info("Getting feed for {}", streamerId);
    final StreamerFeed feed = streamerFeedRepository.get(streamerId);
    return feed.nextNews().map(News::asDto).map(List::of).orElse(List.of());
  }

  private String getOwnerId(Authentication auth) {
    return String.valueOf(
      auth.getAttributes().getOrDefault("preferred_username", "")
    );
  }
}
