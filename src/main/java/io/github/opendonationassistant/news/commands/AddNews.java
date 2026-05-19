package io.github.opendonationassistant.news.commands;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.news.repository.NewsRepository;
import io.github.opendonationassistant.news.view.NewsDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

@Controller
public class AddNews extends BaseController {

  private final NewsRepository newsRepository;

  @Inject
  public AddNews(NewsRepository newsRepository) {
    this.newsRepository = newsRepository;
  }

  @Post("/news/commands/add-news")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<NewsDto> addNews(
    Authentication auth,
    @Body AddNewsCommand command
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(
      newsRepository
        .create(command.title(), command.description(), command.demoUrl(), command.global())
        .asDto()
    );
  }

  @Serdeable
  public static record AddNewsCommand(
    String title,
    String description,
    @Nullable String demoUrl,
    Boolean global
  ) {}
}
