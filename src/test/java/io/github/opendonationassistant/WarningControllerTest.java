package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.warning.repository.WarningData;
import io.github.opendonationassistant.warning.view.WarningController;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class WarningControllerTest {

  @Inject
  WarningController controller;

  @Inject
  Map<String, List<WarningData>> warnings;

  private Authentication auth(String username) {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", username)
    );
    return auth;
  }

  @Test
  public void testGetWarningsForRecipient() {
    var expected = List.of(new WarningData("warning message"));
    warnings.put("streamerId", expected);

    var result = controller.getWarnings(auth("streamerId"));

    assertEquals(expected, result.join().body());
  }

  @Test
  public void testGetWarningsWhenNone() {
    warnings.clear();

    var result = controller.getWarnings(auth("unknown"));

    assertEquals(List.of(), result.join().body());
  }
}
