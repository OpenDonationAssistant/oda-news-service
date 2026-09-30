package io.github.opendonationassistant.feedback.commands;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.feedback.repository.NewsFeedbackRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Controller("/news/{newsId}/feedback/commands")
@Validated
public class FeedbackCommandsController extends BaseController {

  private final NewsFeedbackRepository feedbackRepository;

  @Inject
  public FeedbackCommandsController(NewsFeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  @Post("/create")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> createFeedback(
    @PathVariable @NotBlank String newsId,
    Authentication auth,
    @Valid @Body CreateFeedbackCommand command
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    command.executeWith(newsId, ownerId.get(), feedbackRepository);
    return HttpResponse.ok();
  }
}
