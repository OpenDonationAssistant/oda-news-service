package io.github.opendonationassistant.news.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.news.News;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
class NewsRepositoryIntegrationTest {

  @Inject
  NewsDataRepository dataRepository;

  @Test
  void testSavingAndReadingNews() {
    var repository = new NewsRepository(dataRepository);

    var created = repository.create(
      "First News",
      "Description",
      "2024-01-01",
      "https://demo.url",
      true
    );
    assertNotNull(created);
    assertNotNull(created.getId());
    var expected = new News(
      created.getId(),
      "First News",
      "Description",
      "2024-01-01",
      "https://demo.url",
      dataRepository
    );
    assertEquals(expected, created);

    var loaded = repository.get(created.getId());

    assertTrue(loaded.isPresent());
    assertEquals(created, loaded.get());
    assertEquals(expected, loaded.get());
  }
}
