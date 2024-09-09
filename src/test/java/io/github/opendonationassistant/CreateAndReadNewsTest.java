package io.github.opendonationassistant;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.news.commands.AddNewsCommand;
import io.github.opendonationassistant.news.commands.NewsCommandController;
import io.github.opendonationassistant.news.view.NewsController;
import io.github.opendonationassistant.news.view.NewsDto;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class CreateAndReadNewsTest {

  @Inject
  NewsCommandController commandController;

  @Inject
  NewsController newsController;

  @Test
  public void testCreateAndReadNews(@Given AddNewsCommand command) {
    commandController.createNews(command);

    @NonNull
    final List<NewsDto> news = newsController
      .getNews(Pageable.from(0, 10))
      .getContent();

    assertEquals(command.getTitle(), news.get(0).getTitle());
    assertEquals(command.getDescription(), news.get(0).getDescription());
    assertEquals(command.getDemoUrl(), news.get(0).getDemoUrl());
  }
}
