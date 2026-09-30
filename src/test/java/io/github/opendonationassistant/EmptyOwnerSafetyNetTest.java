package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.feed.repository.StreamerFeedDataRepository;
import io.github.opendonationassistant.feed.view.StreamerFeedController;
import io.github.opendonationassistant.feedback.commands.CreateFeedbackCommand;
import io.github.opendonationassistant.feedback.commands.FeedbackCommandsController;
import io.github.opendonationassistant.feedback.repository.NewsFeedbackDataRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
class EmptyOwnerSafetyNetTest {

  @Inject
  FeedbackCommandsController feedbackCommandsController;

  @Inject
  NewsFeedbackDataRepository feedbackDataRepository;

  @Inject
  StreamerFeedController streamerFeedController;

  @Inject
  StreamerFeedDataRepository feedDataRepository;

  private Authentication authWithoutUsername() {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());
    return auth;
  }

  @Test
  void feedbackWithMissingUsernameReturnsUnauthorized() {
    var response = feedbackCommandsController.createFeedback(
      "news-empty-owner",
      authWithoutUsername(),
      new CreateFeedbackCommand(5)
    );

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    assertFalse(
      feedbackDataRepository
        .findAll()
        .stream()
        .anyMatch(feedback -> "news-empty-owner".equals(feedback.getNewsId()))
    );
  }

  @Test
  void feedReadDoesNotPersistRow() {
    if (feedDataRepository.existsById("")) {
      feedDataRepository.deleteById("");
    }

    streamerFeedController.getFeed(authWithoutUsername());

    assertFalse(feedDataRepository.existsById(""));
  }
}
