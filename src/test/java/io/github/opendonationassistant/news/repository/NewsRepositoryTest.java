package io.github.opendonationassistant.news.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.news.News;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(InstancioExtension.class)
@ExtendWith(MockitoExtension.class)
public class NewsRepositoryTest {

  @Mock
  NewsDataRepository newsDataRepository;

  @Test
  public void testReturningEmptyAfterLastNews(@Given NewsData last) {
    when(newsDataRepository.findAll()).thenReturn(List.of(last));
    Optional<News> actual = new NewsRepository(newsDataRepository)
      .nextAfter(last.getId());
    assertTrue(actual.isEmpty());
  }

  @Test
  public void testReturningLastNews(
    @Given NewsData first,
    @Given NewsData second
  ) throws InterruptedException {
    first.setId(Generators.timeBasedEpochGenerator().generate().toString());
    Thread.sleep(10);
    second.setId(Generators.timeBasedEpochGenerator().generate().toString());
    when(newsDataRepository.findAll()).thenReturn(List.of(second, first));
    Optional<News> actual = new NewsRepository(newsDataRepository).last();
    assertTrue(actual.isPresent());
    assertEquals(second.asNews(newsDataRepository), actual.get());
  }

  @Test
  public void testReturningNextNews(
    @Given NewsData first,
    @Given NewsData second,
    @Given NewsData third
  ) throws InterruptedException {
    first.setId(Generators.timeBasedEpochGenerator().generate().toString());
    Thread.sleep(10);
    second.setId(Generators.timeBasedEpochGenerator().generate().toString());
    Thread.sleep(10);
    third.setId(Generators.timeBasedEpochGenerator().generate().toString());

    when(newsDataRepository.findAll())
      .thenReturn(List.of(third, first, second));

    Optional<News> actual = new NewsRepository(newsDataRepository)
      .nextAfter(first.getId());
    assertTrue(actual.isPresent());
    assertEquals(second.asNews(newsDataRepository), actual.get());

    actual = new NewsRepository(newsDataRepository).nextAfter(second.getId());
    assertTrue(actual.isPresent());
    assertEquals(third.asNews(newsDataRepository), actual.get());
  }
}
