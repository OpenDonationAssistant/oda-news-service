package io.github.opendonationassistant.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.opendonationassistant.feed.repository.StreamerFeedRepository;
import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.repository.NewsData;
import io.github.opendonationassistant.news.repository.NewsDataRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class StreamerFeedTest {

  @Inject
  StreamerFeedRepository feedRepository;

  @Inject
  NewsDataRepository newsDataRepository;

  @Test
  public void testMarkAsRead() {
    var first = new NewsData(
      "1",
      "Title 1",
      "Description 1",
      "https://demo.url"
    );
    var second = new NewsData(
      "2",
      "Title 2",
      "Description 2",
      "https://demo.url"
    );
    var third = new NewsData(
      "3",
      "Title 3",
      "Description 3",
      "https://demo.url"
    );

    newsDataRepository.save(first);
    StreamerFeed feed = feedRepository.get("streamerId");
    final Optional<News> next = feed.nextNews();
    assertEquals(Optional.of(first.asNews(newsDataRepository)), next);

    feed.markAsRead("1");
    feed = feedRepository.get("streamerId");
    final Optional<News> shouldBeEmpty = feed.nextNews();
    assertEquals(Optional.empty(), shouldBeEmpty);

    newsDataRepository.save(second);
    newsDataRepository.save(third);
    feed = feedRepository.get("streamerId");
    final Optional<News> shouldBeSecond = feed.nextNews();
    assertEquals(
      Optional.of(second.asNews(newsDataRepository)),
      shouldBeSecond
    );
  }
}
