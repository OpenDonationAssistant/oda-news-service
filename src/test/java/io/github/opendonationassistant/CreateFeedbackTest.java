package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.feedback.commands.CreateFeedbackCommand;
import io.github.opendonationassistant.feedback.commands.FeedbackCommandsController;
import io.github.opendonationassistant.feedback.repository.NewsFeedbackData;
import io.github.opendonationassistant.feedback.repository.NewsFeedbackDataRepository;
import io.github.opendonationassistant.feedback.repository.NewsFeedbackRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class CreateFeedbackTest {

  @Inject
  FeedbackCommandsController controller;

  @Inject
  NewsFeedbackDataRepository repository;

  @Test
  public void testCreatingFeedback() {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes())
      .thenReturn(Map.of("preferred_username", "streamerId"));

    var command = new CreateFeedbackCommand(5);

    controller.createFeedback("newsId", auth, command);
    repository
      .findAll()
      .contains(new NewsFeedbackData("newsId", "streamerId", 5));
  }
}
