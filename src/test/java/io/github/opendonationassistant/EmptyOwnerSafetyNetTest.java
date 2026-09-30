package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.feed.repository.StreamerFeedDataRepository;
import io.github.opendonationassistant.feed.view.StreamerFeedController;
import io.github.opendonationassistant.feedback.commands.CreateFeedbackCommand;
import io.github.opendonationassistant.feedback.commands.FeedbackCommandsController;
import io.github.opendonationassistant.feedback.repository.NewsFeedbackDataRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Phase 0 safety net for controllers that inline {@code getOwnerId}.
 *
 * <p>These controllers fall back to the empty string when the token has no
 * {@code preferred_username} claim instead of rejecting the request. Both tests characterize the
 * current behavior so Phase 3 can flip them to expect a 401.
 */
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
  void feedbackIsStoredUnderEmptyOwnerWhenUsernameIsMissing() {
    // Characterization of defect #9: FeedbackCommandsController.getOwnerId returns "" when the
    // claim is absent, so feedback is persisted under an empty owner. Phase 3 should return 401.
    feedbackCommandsController.createFeedback(
      "news-empty-owner",
      authWithoutUsername(),
      new CreateFeedbackCommand(5)
    );

    assertTrue(
      feedbackDataRepository
        .findAll()
        .stream()
        .anyMatch(feedback ->
          "news-empty-owner".equals(feedback.getNewsId()) &&
          "".equals(feedback.getStreamerId())
        )
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
