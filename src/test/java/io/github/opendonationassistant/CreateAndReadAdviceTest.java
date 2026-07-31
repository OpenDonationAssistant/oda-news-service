package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.opendonationassistant.advice.commands.AddAdvice;
import io.github.opendonationassistant.advice.commands.AddAdvice.AddAdviceCommand;
import io.github.opendonationassistant.advice.commands.GetRandomAdvice;
import io.github.opendonationassistant.advice.repository.AdviceDataRepository;
import io.github.opendonationassistant.advice.view.AdviceDto;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Set;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class CreateAndReadAdviceTest {

  @Inject
  AddAdvice addAdvice;

  @Inject
  GetRandomAdvice getRandomAdvice;

  @Inject
  AdviceDataRepository adviceDataRepository;

  private Authentication auth() {
    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", "testuser")
    );
    return auth;
  }

  @Test
  public void testCreateAndReadAdvice(@Given AddAdviceCommand command) {
    AdviceDto created = addAdvice.addAdvice(auth(), command).body();

    assertNotNull(created.id());
    assertEquals(command.text(), created.text());

    var saved = adviceDataRepository.findById(created.id());
    assertEquals(command.text(), saved.get().getText());
  }

  @Test
  public void testGetRandomAdviceReturnsCreatedAdvice(
    @Given AddAdviceCommand first,
    @Given AddAdviceCommand second
  ) {
    Set<String> texts = Set.of(first.text(), second.text());
    addAdvice.addAdvice(auth(), first);
    addAdvice.addAdvice(auth(), second);

    AdviceDto random = getRandomAdvice.getRandomAdvice(auth()).body();

    assertNotNull(random.id());
    assertTrue(texts.contains(random.text()));
  }

  @Test
  public void testGetRandomAdviceWhenEmpty() {
    adviceDataRepository.deleteAll();
    var response = getRandomAdvice.getRandomAdvice(auth());
    assertTrue(response.getStatus().getCode() == 404);
  }
}
