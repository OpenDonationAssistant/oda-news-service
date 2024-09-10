package io.github.opendonationassistant.news.view;

import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.repository.NewsRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.annotation.Nonnull;

@Controller
public class NewsController {

  private final NewsRepository newsRepository;

  public NewsController(NewsRepository newsRepository) {
    this.newsRepository = newsRepository;
  }

  @Get("/news")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public @Nonnull Page<NewsDto> getNews(@Nonnull Pageable pageable) {
    return newsRepository.list(pageable).map(News::asDto);
  }
}
