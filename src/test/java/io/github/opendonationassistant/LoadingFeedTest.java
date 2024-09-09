package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.feed.commands.MarkAsReadCommand;
import io.github.opendonationassistant.feed.commands.StreamerFeedCommandController;
import io.github.opendonationassistant.feed.view.StreamerFeedController;
import io.github.opendonationassistant.news.commands.AddNewsCommand;
import io.github.opendonationassistant.news.commands.NewsCommandController;
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
  NewsCommandController newsCommandController;

  @Inject
  StreamerFeedCommandController feedCommandController;

  @Test
  public void testGetNextNewsInFeed(
    @Given AddNewsCommand first,
    @Given AddNewsCommand second,
    @Given AddNewsCommand third
  ) {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes())
      .thenReturn(Map.of("preferred_username", "streamerId"));

    final List<NewsDto> shouldBeEmpty = controller.getFeed(auth);
    assertTrue(shouldBeEmpty.isEmpty());

    newsCommandController.createNews(first);
    final List<NewsDto> shouldBeOne = controller.getFeed(auth);
    assertEquals(1, shouldBeOne.size());
    assertEquals(first.getTitle(), shouldBeOne.get(0).getTitle());
    assertEquals(first.getDescription(), shouldBeOne.get(0).getDescription());
    assertEquals(first.getDemoUrl(), shouldBeOne.get(0).getDemoUrl());

    var markAsReadCommand = new MarkAsReadCommand();
    markAsReadCommand.setNewsId(shouldBeOne.get(0).getId());
    feedCommandController.markAsRead(auth, markAsReadCommand);

    final List<NewsDto> shouldBeEmptyAgain = controller.getFeed(auth);
    assertEquals(List.of(), shouldBeEmptyAgain);

    newsCommandController.createNews(second);
    final List<NewsDto> shouldBeSecond = controller.getFeed(auth);
    assertEquals(1, shouldBeSecond.size());
    assertEquals(second.getTitle(), shouldBeSecond.get(0).getTitle());
    assertEquals(
      second.getDescription(),
      shouldBeSecond.get(0).getDescription()
    );
    assertEquals(second.getDemoUrl(), shouldBeSecond.get(0).getDemoUrl());
  }
}
