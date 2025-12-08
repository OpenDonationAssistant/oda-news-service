package io.github.opendonationassistant.news.commands;

import org.jspecify.annotations.Nullable;

import io.github.opendonationassistant.news.repository.NewsRepository;
import io.github.opendonationassistant.news.view.NewsDto;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;

@Controller
public class AddNews {

  private final NewsRepository newsRepository;

  @Inject
  public AddNews(NewsRepository newsRepository) {
    this.newsRepository = newsRepository;
  }

  @Post("/news/commands/create")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public NewsDto addNews(@Body AddNewsCommand command) {
    return newsRepository
      .create(
        command.title(),
        command.description(),
        command.date(),
        command.demoUrl()
      )
      .asDto();
  }

  @Serdeable
  public static record AddNewsCommand(
    String title,
    String description,
    String date,
    @Nullable String demoUrl
  ) {}
}
