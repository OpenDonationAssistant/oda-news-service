package io.github.opendonationassistant.feed.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.feed.StreamerFeed;
import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.repository.NewsDataRepository;
import io.github.opendonationassistant.news.repository.NewsRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
@ExtendWith(MockitoExtension.class)
public class StreamerFeedRepositoryIntegrationTest {

  @Inject
  StreamerFeedDataRepository dataRepository;

  @Mock
  NewsRepository newsRepository;

  @Mock
  NewsDataRepository newsDataRepository;

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
    News first = new News("1", "Title 1", "Description 1", "2024-01-01", "https://demo.url", newsDataRepository);
    News second = new News("2", "Title 2", "Description 2", "2024-01-01", "https://demo.url", newsDataRepository);
    when(newsRepository.nextAfter(first.getId())).thenReturn(Optional.of(second));
    final var repository = new StreamerFeedRepository(
      dataRepository,
      newsRepository
    );
    final StreamerFeed feed = repository.get(streamerId);
    feed.markAsRead(first.getId());
    final Optional<News> nextNews = feed.nextNews();
    assertEquals(Optional.of(second), nextNews);
  }
}
