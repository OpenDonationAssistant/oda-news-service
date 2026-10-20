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
import java.util.Optional;
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
    var oldTimestamp = System.currentTimeMillis() - 200_000;
    var expected = List.of(
      new WarningData("warning message", null, oldTimestamp)
    );
    warnings.put("streamerId", expected);

    var result = controller.getWarnings(auth("streamerId"), Optional.empty());

    assertEquals(expected, result.body());
  }

  @Test
  public void testGetWarningsFiltersRecentWarnings() {
    var recentTimestamp = System.currentTimeMillis();
    var oldTimestamp = System.currentTimeMillis() - 200_000;
    warnings.put(
      "streamerFilter",
      new java.util.ArrayList<>(
        List.of(
          new WarningData("recent", null, recentTimestamp),
          new WarningData("old", null, oldTimestamp)
        )
      )
    );

    var result = controller.getWarnings(auth("streamerFilter"), Optional.empty());

    assertEquals(
      List.of(new WarningData("old", null, oldTimestamp)),
      result.body()
    );
  }

  @Test
  public void testGetWarningsWhenNone() {
    warnings.clear();

    var result = controller.getWarnings(auth("unknown"), Optional.empty());

    assertEquals(List.of(), result.body());
  }

  @Test
  public void testGetWarningsHidesFreshOnlyWarnings() {
    // Intentional behavior: warnings younger than 180s are not returned yet.
    var freshTimestamp = System.currentTimeMillis();
    warnings.put(
      "streamerFreshOnly",
      new java.util.ArrayList<>(
        List.of(new WarningData("fresh", null, freshTimestamp))
      )
    );

    var result = controller.getWarnings(auth("streamerFreshOnly"), Optional.empty());

    assertEquals(List.of(), result.body());
  }

  @Test
  public void testGetWarningsFiltersByPriority() {
    var oldTimestamp = System.currentTimeMillis() - 200_000;
    var error = new WarningData(
      "error message",
      null,
      oldTimestamp,
      "Error"
    );
    warnings.put(
      "streamerPriority",
      new java.util.ArrayList<>(
        List.of(error, new WarningData("notification", null, oldTimestamp))
      )
    );

    var result = controller.getWarnings(
      auth("streamerPriority"),
      Optional.of("Error")
    );

    assertEquals(List.of(error), result.body());
  }

  @Test
  public void testGetWarningsWithUnmatchedPriorityReturnsEmpty() {
    var oldTimestamp = System.currentTimeMillis() - 200_000;
    warnings.put(
      "streamerUnmatchedPriority",
      new java.util.ArrayList<>(
        List.of(new WarningData("notification", null, oldTimestamp))
      )
    );

    var result = controller.getWarnings(
      auth("streamerUnmatchedPriority"),
      Optional.of("Error")
    );

    assertEquals(List.of(), result.body());
  }
}
