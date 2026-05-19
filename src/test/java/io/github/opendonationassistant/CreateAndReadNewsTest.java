package io.github.opendonationassistant;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.news.commands.AddNews;
import io.github.opendonationassistant.news.commands.AddNews.AddNewsCommand;
import io.github.opendonationassistant.news.view.NewsController;
import io.github.opendonationassistant.news.view.NewsDto;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Pageable;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class CreateAndReadNewsTest {

  @Inject
  AddNews addNews;

  @Inject
  NewsController newsController;

  @Test
  public void testCreateAndReadNews(@Given AddNewsCommand command) {
    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", "testuser")
    );
    addNews.addNews(auth, command);

    @NonNull
    final List<NewsDto> news = newsController
      .getNews(Pageable.from(0, 10))
      .getContent();

    assertEquals(command.title(), news.get(0).title());
    assertEquals(command.description(), news.get(0).description());
    assertEquals(command.demoUrl(), news.get(0).demoUrl());
  }
}
