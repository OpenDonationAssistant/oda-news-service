package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.opendonationassistant.guides.commands.MarkRead;
import io.github.opendonationassistant.guides.commands.MarkRead.MarkReadCommand;
import io.github.opendonationassistant.guides.repository.GuidesDataRepository;
import io.github.opendonationassistant.guides.view.GetReadGuides;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Map;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class CreateAndReadGuidesTest {

  @Inject
  MarkRead markRead;

  @Inject
  GetReadGuides getReadGuides;

  @Inject
  GuidesDataRepository guidesDataRepository;

  private Authentication auth(String username) {
    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", username)
    );
    return auth;
  }

  @Test
  public void testMarkReadAndGetReadGuides(@Given MarkReadCommand command) {
    markRead.markRead(auth("testuser"), command);

    var response = getReadGuides.getReadGuides(auth("testuser"));
    assertTrue(response.body().ids().contains(command.guideId()));
  }

  @Test
  public void testGetReadGuidesWhenEmpty() {
    var response = getReadGuides.getReadGuides(auth("emptyuser"));
    assertTrue(response.body().ids().isEmpty());
  }
}
