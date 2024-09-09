package io.github.opendonationassistant.news.commands;

import io.github.opendonationassistant.news.repository.NewsRepository;
import io.github.opendonationassistant.news.view.NewsDto;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

@Controller("/news/commands")
public class NewsCommandController {

  private final NewsRepository newsRepository;

  @Inject
  public NewsCommandController(NewsRepository newsRepository) {
    this.newsRepository = newsRepository;
  }

  @Post("/create")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public NewsDto createNews(@Body AddNewsCommand command) {
    return command.executeWith(newsRepository).asDto();
  }
}
