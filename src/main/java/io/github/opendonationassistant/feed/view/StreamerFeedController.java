package io.github.opendonationassistant.feed.view;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.feed.StreamerFeed;
import io.github.opendonationassistant.feed.repository.StreamerFeedRepository;
import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.view.NewsDto;
import io.micronaut.http.HttpResponse;
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
public class StreamerFeedController extends BaseController {

  private Logger log = LoggerFactory.getLogger(StreamerFeedController.class);

  private final StreamerFeedRepository streamerFeedRepository;

  public StreamerFeedController(StreamerFeedRepository streamerFeedRepository) {
    this.streamerFeedRepository = streamerFeedRepository;
  }

  @Get("/feed/news")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<List<NewsDto>> getFeed(@Nonnull Authentication auth) {
    var streamerId = getOwnerId(auth);
    if (streamerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    log.info("Getting feed for {}", streamerId.get());
    final StreamerFeed feed = streamerFeedRepository.get(streamerId.get());
    return HttpResponse.ok(
      feed.nextNews().map(News::asDto).map(List::of).orElse(List.of())
    );
  }
}
