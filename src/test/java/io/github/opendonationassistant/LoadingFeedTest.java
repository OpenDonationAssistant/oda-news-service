package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.feed.commands.MarkAsRead;
import io.github.opendonationassistant.feed.view.StreamerFeedController;
import io.github.opendonationassistant.news.commands.AddNews;
import io.github.opendonationassistant.news.commands.AddNews.AddNewsCommand;
import io.github.opendonationassistant.news.view.NewsDto;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class LoadingFeedTest {

  @Inject
  StreamerFeedController controller;

  @Inject
  AddNews addNews;

  @Inject
  MarkAsRead markAsRead;

  @Test
  public void testGetNextNewsInFeed(
    @Given AddNewsCommand first,
    @Given AddNewsCommand second,
    @Given AddNewsCommand third
  ) {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", "streamerId")
    );

    final List<NewsDto> shouldBeEmpty = controller.getFeed(auth);
    assertTrue(shouldBeEmpty.isEmpty());

    addNews.addNews(first);
    final List<NewsDto> shouldBeOne = controller.getFeed(auth);
    assertEquals(1, shouldBeOne.size());
    assertEquals(first.title(), shouldBeOne.get(0).title());
    assertEquals(first.description(), shouldBeOne.get(0).description());
    assertEquals(first.demoUrl(), shouldBeOne.get(0).demoUrl());

    var markAsReadCommand = new MarkAsRead.MarkAsReadCommand(
      shouldBeOne.get(0).id()
    );
    markAsRead.markAsRead(auth, markAsReadCommand);

    final List<NewsDto> shouldBeEmptyAgain = controller.getFeed(auth);
    assertEquals(List.of(), shouldBeEmptyAgain);

    addNews.addNews(second);
    final List<NewsDto> shouldBeSecond = controller.getFeed(auth);
    assertEquals(1, shouldBeSecond.size());
    assertEquals(second.title(), shouldBeSecond.get(0).title());
    assertEquals(second.description(), shouldBeSecond.get(0).description());
    assertEquals(second.demoUrl(), shouldBeSecond.get(0).demoUrl());
  }
}
