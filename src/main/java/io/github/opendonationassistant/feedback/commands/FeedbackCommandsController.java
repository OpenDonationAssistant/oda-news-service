package io.github.opendonationassistant.feedback.commands;

import io.github.opendonationassistant.feedback.repository.NewsFeedbackRepository;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

@Controller("/news/{newsId}/feedback/commands")
public class FeedbackCommandsController {

  private final NewsFeedbackRepository feedbackRepository;

  @Inject
  public FeedbackCommandsController(NewsFeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  @Post("/create")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public void createFeedback(@PathVariable String newsId, Authentication auth, @Body CreateFeedbackCommand command) {
    command.executeWith(newsId, getOwnerId(auth), feedbackRepository);
  }

  private String getOwnerId(Authentication auth) {
    return String.valueOf(
      auth.getAttributes().getOrDefault("preferred_username", "")
    );
  }

}
