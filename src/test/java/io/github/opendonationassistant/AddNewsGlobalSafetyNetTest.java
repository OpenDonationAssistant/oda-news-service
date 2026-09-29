package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.news.commands.AddNews;
import io.github.opendonationassistant.news.commands.AddNews.AddNewsCommand;
import io.github.opendonationassistant.news.repository.NewsDataRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Phase 0 safety net for the {@code global} flag on news creation.
 *
 * <p>Locks the round-trip of a supplied {@code global} value and characterizes the known unboxing
 * defect when the field is omitted.
 */
@MicronautTest(environments = "allinone")
class AddNewsGlobalSafetyNetTest {

  @Inject
  AddNews addNews;

  @Inject
  NewsDataRepository newsDataRepository;

  private Authentication auth() {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", "testuser")
    );
    return auth;
  }

  @Test
  void createNewsPersistsGlobalTrue() {
    var created = addNews
      .addNews(auth(), new AddNewsCommand("title-true", "desc", null, true))
      .body();

    assertTrue(
      newsDataRepository.findById(created.id()).orElseThrow().isGlobal()
    );
  }

  @Test
  void createNewsPersistsGlobalFalse() {
    var created = addNews
      .addNews(auth(), new AddNewsCommand("title-false", "desc", null, false))
      .body();

    assertFalse(
      newsDataRepository.findById(created.id()).orElseThrow().isGlobal()
    );
  }

  @Test
  void createNewsThrowsWhenGlobalIsOmitted() {
    // Characterization of defect #1: the Boolean command field is unboxed into a
    // primitive boolean argument, so an omitted `global` yields a NullPointerException.
    // Phase 1 should default an omitted global to true instead of throwing.
    assertThrows(NullPointerException.class, () ->
      addNews.addNews(
        auth(),
        new AddNewsCommand("title-null", "desc", null, null)
      )
    );
  }
}
