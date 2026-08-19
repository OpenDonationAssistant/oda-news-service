package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.warning.commands.WarningCommandsController;
import io.github.opendonationassistant.warning.repository.WarningData;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class WarningCommandsControllerTest {

  @Inject
  WarningCommandsController controller;

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
  public void testClearWarningsForRecipient() {
    warnings.put("streamerId", List.of(new WarningData("warning message")));

    var response = controller.clearWarnings(auth("streamerId"));

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertTrue(
      warnings.get("streamerId") == null || warnings.get("streamerId").isEmpty()
    );
  }

  @Test
  public void testClearWarningsDoesNotAffectOtherRecipients() {
    warnings.put("streamerA", List.of(new WarningData("mine")));
    warnings.put("streamerB", List.of(new WarningData("theirs")));

    var response = controller.clearWarnings(auth("streamerA"));

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertTrue(warnings.get("streamerA") == null);
    assertEquals(1, warnings.get("streamerB").size());
  }

  @Test
  public void testClearWarningsWithUnknownUserReturnsUnauthorized() {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());

    var response = controller.clearWarnings(auth);

    assertEquals(HttpStatus.UNAUTHORIZED, response.join().getStatus());
  }

  @Test
  public void testAuthorizedUserCanCreateWarningForRecipient() {
    var command = new WarningCommandsController.AddWarningCommand(
      "you have been warned"
    );

    var response = controller.addWarning(auth("streamerId"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(
      List.of(new WarningData("you have been warned")),
      warnings.get("streamerId")
    );
  }

  @Test
  public void testCreateWarningAppendsToExistingWarnings() {
    warnings.put(
      "streamerId",
      new java.util.ArrayList<>(List.of(new WarningData("first")))
    );
    var command = new WarningCommandsController.AddWarningCommand(
      "second warning"
    );

    var response = controller.addWarning(auth("streamerId"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(2, warnings.get("streamerId").size());
  }
}
