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

  private static WarningData warning(String message) {
    return new WarningData(message, null, 1000L);
  }

  private static WarningData warning(String message, String component) {
    return new WarningData(message, component, 1000L);
  }

  @Test
  public void testClearWarningsForRecipient() {
    warnings.put("streamerId", List.of(warning("warning message")));

    var command = new WarningCommandsController.ClearWarningsCommand(null);
    var response = controller.clearWarnings(auth("streamerId"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertTrue(
      warnings.get("streamerId") == null || warnings.get("streamerId").isEmpty()
    );
  }

  @Test
  public void testClearWarningsDoesNotAffectOtherRecipients() {
    warnings.put("streamerA", List.of(warning("mine")));
    warnings.put("streamerB", List.of(warning("theirs")));

    var command = new WarningCommandsController.ClearWarningsCommand(null);
    var response = controller.clearWarnings(auth("streamerA"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertTrue(warnings.get("streamerA") == null);
    assertEquals(1, warnings.get("streamerB").size());
  }

  @Test
  public void testClearWarningsWithUnknownUserReturnsUnauthorized() {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());

    var command = new WarningCommandsController.ClearWarningsCommand(null);
    var response = controller.clearWarnings(auth, command);

    assertEquals(HttpStatus.UNAUTHORIZED, response.join().getStatus());
  }

  @Test
  public void testClearWarningsByComponents() {
    warnings.put(
      "streamerByComponents",
      new java.util.ArrayList<>(
        List.of(
          warning("chat warning", "chat"),
          warning("donation warning", "donation"),
          warning("no component")
        )
      )
    );

    var command = new WarningCommandsController.ClearWarningsCommand(
      List.of("chat")
    );
    var response = controller.clearWarnings(auth("streamerByComponents"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(
      List.of(
        warning("donation warning", "donation"),
        warning("no component")
      ),
      warnings.get("streamerByComponents")
    );
  }

  @Test
  public void testClearWarningsWithEmptyComponentsDeletesNothing() {
    warnings.put(
      "streamerEmptyComponents",
      new java.util.ArrayList<>(
        List.of(warning("chat warning", "chat"))
      )
    );

    var command = new WarningCommandsController.ClearWarningsCommand(
      List.of()
    );
    var response = controller.clearWarnings(
      auth("streamerEmptyComponents"),
      command
    );

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(
      List.of(warning("chat warning", "chat")),
      warnings.get("streamerEmptyComponents")
    );
  }

  @Test
  public void testAuthorizedUserCanCreateWarningForRecipient() {
    var command = new WarningCommandsController.AddWarningCommand(
      "you have been warned"
    );

    var response = controller.addWarning(auth("streamerId"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(1, warnings.get("streamerId").size());
    assertEquals("you have been warned", warnings.get("streamerId").get(0).message());
    assertEquals(null, warnings.get("streamerId").get(0).component());
    assertEquals("Notification", warnings.get("streamerId").get(0).priority());
  }

  @Test
  public void testCreateWarningAppendsToExistingWarnings() {
    warnings.put(
      "streamerId",
      new java.util.ArrayList<>(List.of(warning("first")))
    );
    var command = new WarningCommandsController.AddWarningCommand(
      "second warning"
    );

    var response = controller.addWarning(auth("streamerId"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(2, warnings.get("streamerId").size());
  }

  @Test
  public void testCreateWarningWithComponent() {
    var command = new WarningCommandsController.AddWarningCommand(
      "you have been warned",
      "chat"
    );

    var response = controller.addWarning(auth("streamerWithComponent"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(1, warnings.get("streamerWithComponent").size());
    assertEquals("you have been warned", warnings.get("streamerWithComponent").get(0).message());
    assertEquals("chat", warnings.get("streamerWithComponent").get(0).component());
    assertEquals("Notification", warnings.get("streamerWithComponent").get(0).priority());
  }

  @Test
  public void testCreateWarningWithPriority() {
    var command = new WarningCommandsController.AddWarningCommand(
      "you have been warned",
      null,
      "Critical"
    );

    var response = controller.addWarning(auth("streamerWithPriority"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(1, warnings.get("streamerWithPriority").size());
    assertEquals("you have been warned", warnings.get("streamerWithPriority").get(0).message());
    assertEquals("Critical", warnings.get("streamerWithPriority").get(0).priority());
  }

  @Test
  public void testAddWarningOverridesExistingWithSameComponent() {
    warnings.put(
      "streamerOverride",
      new java.util.ArrayList<>(
        List.of(
          warning("old chat warning", "chat"),
          warning("donation warning", "donation")
        )
      )
    );
    var command = new WarningCommandsController.AddWarningCommand(
      "new chat warning",
      "chat"
    );

    var response = controller.addWarning(
      auth("streamerOverride"),
      command
    );

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(2, warnings.get("streamerOverride").size());
    assertEquals("new chat warning", warnings.get("streamerOverride").get(0).message());
    assertEquals("chat", warnings.get("streamerOverride").get(0).component());
    assertEquals("donation warning", warnings.get("streamerOverride").get(1).message());
    assertEquals("donation", warnings.get("streamerOverride").get(1).component());
  }

  @Test
  public void testAddWarningWithoutComponentAlwaysAppends() {
    warnings.put(
      "streamerNoComp",
      new java.util.ArrayList<>(
        List.of(warning("first"))
      )
    );
    var command = new WarningCommandsController.AddWarningCommand("second");

    var response = controller.addWarning(auth("streamerNoComp"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(2, warnings.get("streamerNoComp").size());
  }

  @Test
  public void testCreateWarningWithoutComponentDefaultsToNull() {
    var command = new WarningCommandsController.AddWarningCommand(
      "you have been warned"
    );

    var response = controller.addWarning(auth("streamerNoComponent"), command);

    assertEquals(HttpStatus.OK, response.join().getStatus());
    assertEquals(1, warnings.get("streamerNoComponent").size());
    assertEquals("you have been warned", warnings.get("streamerNoComponent").get(0).message());
    assertEquals(null, warnings.get("streamerNoComponent").get(0).component());
    assertEquals("Notification", warnings.get("streamerNoComponent").get(0).priority());
  }
}
