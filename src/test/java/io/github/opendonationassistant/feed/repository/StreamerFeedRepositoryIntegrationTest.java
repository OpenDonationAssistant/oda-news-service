package io.github.opendonationassistant.feed.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import io.github.opendonationassistant.feed.StreamerFeed;
import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.repository.NewsData;
import io.github.opendonationassistant.news.repository.NewsDataRepository;
import io.github.opendonationassistant.news.repository.NewsRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class StreamerFeedRepositoryIntegrationTest {

  @Inject
  StreamerFeedDataRepository dataRepository;

  NewsRepository newsRepository = mock(NewsRepository.class);

  NewsDataRepository newsDataRepository = mock(NewsDataRepository.class);

  @Test
  public void testCreateAndLoadStreamerFeed(
    @Given String streamerId,
    @Given News news
  ) {
    var expectedNextNews = Optional.of(news);
    when(newsRepository.last()).thenReturn(expectedNextNews);
    assertFalse(dataRepository.existsById(streamerId));
    final var repository = new StreamerFeedRepository(
      dataRepository,
      newsRepository
    );
    final StreamerFeed feed = repository.get(streamerId);
    assertTrue(dataRepository.existsById(streamerId));
    final Optional<News> nextNews = feed.nextNews();
    assertEquals(expectedNextNews, nextNews);
  }

  @Test
  public void testGettingNextNews(@Given String streamerId) {
    var first = new NewsData(
      "1",
      "Title 1",
      "Description 1",
      "2024-01-01",
      "https://demo.url"
    );
    var second = new NewsData(
      "2",
      "Title 2",
      "Description 2",
      "2024-01-01",
      "https://demo.url"
    );
    when(newsDataRepository.findAll()).thenReturn(List.of(first, second));
    var newsRepository = new NewsRepository(newsDataRepository);
    final var repository = new StreamerFeedRepository(
      dataRepository,
      newsRepository
    );
    final StreamerFeed feed = repository.get(streamerId);
    feed.markAsRead(first.getId());
    final Optional<News> nextNews = feed.nextNews();
    assertEquals(
      Optional.of(
        new News(
          "2",
          "Title 2",
          "Description 2",
          "2024-01-01",
          "https://demo.url",
          newsDataRepository
        )
      ),
      nextNews
    );
  }
}
