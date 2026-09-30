package io.github.opendonationassistant.feed.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
class StreamerFeedDataRepositoryIntegrationTest {

  @Inject
  StreamerFeedDataRepository repository;

  private List<StreamerFeedData> rowsFor(String streamerId) {
    return repository
      .findAll()
      .stream()
      .filter(row -> streamerId.equals(row.getStreamerId()))
      .toList();
  }

  @Test
  void upsertInsertsRowWhenAbsent() {
    var streamerId = "repo-upsert-insert";
    if (repository.existsById(streamerId)) {
      repository.deleteById(streamerId);
    }

    repository.upsert(streamerId, "news-1");

    var rows = rowsFor(streamerId);
    assertEquals(1, rows.size());
    assertEquals("news-1", rows.get(0).getLastReadNewsId());
  }

  @Test
  void upsertUpdatesRowWhenPresent() {
    var streamerId = "repo-upsert-update";

    repository.upsert(streamerId, "news-1");
    repository.upsert(streamerId, "news-2");

    var rows = rowsFor(streamerId);
    assertEquals(1, rows.size());
    assertEquals("news-2", rows.get(0).getLastReadNewsId());
  }

  @Test
  void upsertIsIdempotentAndKeepsASingleRow() {
    var streamerId = "repo-upsert-idempotent";

    repository.upsert(streamerId, "news-1");
    repository.upsert(streamerId, "news-1");

    var rows = rowsFor(streamerId);
    assertEquals(1, rows.size());
    assertEquals("news-1", rows.get(0).getLastReadNewsId());
  }

  @Test
  void upsertDoesNotAffectOtherStreamers() {
    repository.upsert("repo-upsert-a", "news-a");
    repository.upsert("repo-upsert-b", "news-b");

    assertEquals(
      "news-a",
      repository.findById("repo-upsert-a").orElseThrow().getLastReadNewsId()
    );
    assertEquals(
      "news-b",
      repository.findById("repo-upsert-b").orElseThrow().getLastReadNewsId()
    );
  }

  @Test
  void concurrentUpsertsKeepExactlyOneRow() throws Exception {
    var streamerId = "repo-upsert-concurrent";
    int attempts = 20;
    var pool = Executors.newFixedThreadPool(8);
    try {
      List<Future<?>> futures = new ArrayList<>();
      for (int i = 0; i < attempts; i++) {
        final int index = i;
        futures.add(
          pool.submit(() -> repository.upsert(streamerId, "news-" + index))
        );
      }
      for (var future : futures) {
        future.get(10, TimeUnit.SECONDS);
      }
    } finally {
      pool.shutdownNow();
    }

    // ON CONFLICT (streamer_id) must never create duplicates under concurrency.
    assertEquals(1, rowsFor(streamerId).size());
  }
}
