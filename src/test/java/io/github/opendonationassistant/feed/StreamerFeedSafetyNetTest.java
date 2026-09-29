package io.github.opendonationassistant.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.feed.repository.StreamerFeedDataRepository;
import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.repository.NewsDataRepository;
import io.github.opendonationassistant.news.repository.NewsRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Phase 0 safety net for {@link StreamerFeed}.
 *
 * <p>Locks the intended "newest news only" semantics of {@code nextNews()} and the lexicographic
 * contract of {@code hasRead()} so later refactors cannot silently change them. The
 * {@code hasRead(null)} case characterizes a known defect; see the test comment.
 */
// NullAway does not model StreamerFeed's jakarta.annotation.Nullable constructor argument, so the
// intentional null-boundary cases below need an explicit suppression.
@SuppressWarnings("NullAway")
class StreamerFeedSafetyNetTest {

  private final NewsDataRepository newsDataRepository = mock(NewsDataRepository.class);
  private final StreamerFeedDataRepository feedDataRepository =
    mock(StreamerFeedDataRepository.class);
  private final NewsRepository newsRepository = mock(NewsRepository.class);

  private News news(String id) {
    return new News(id, "title-" + id, "description-" + id, "2026-01-01", null, newsDataRepository);
  }

  private StreamerFeed feed(String lastReadNewsId) {
    return new StreamerFeed("streamer", lastReadNewsId, feedDataRepository, newsRepository);
  }

  @Test
  void nextNewsReturnsNewestItemWhenOlderItemsAreUnread() {
    // Arrange: three items exist and only the oldest has been read.
    var first = news("1");
    var second = news("2");
    var third = news("3");
    when(newsRepository.last()).thenReturn(Optional.of(third));

    // Act
    var next = feed(first.getId()).nextNews();

    // Assert: the newest item wins; the middle unread item is intentionally skipped.
    assertEquals(Optional.of(third), next);
    assertNotEquals(Optional.of(second), next);
  }

  @Test
  void nextNewsIsEmptyWhenTheNewestItemHasBeenRead() {
    // Arrange/Act: the newest item has been read, older items are still unread.
    var third = news("3");
    when(newsRepository.last()).thenReturn(Optional.of(third));

    // Assert: reading the newest clears the feed even though "1" and "2" are unread.
    assertEquals(Optional.empty(), feed(third.getId()).nextNews());
  }

  @Test
  void nextNewsReturnsNewestItemWhenNothingHasBeenRead() {
    var third = news("3");
    when(newsRepository.last()).thenReturn(Optional.of(third));

    assertEquals(Optional.of(third), feed(null).nextNews());
  }

  @Test
  void nextNewsIsEmptyWhenNoNewsExist() {
    when(newsRepository.last()).thenReturn(Optional.empty());

    assertEquals(Optional.empty(), feed(null).nextNews());
  }

  @Test
  void hasReadComparesLexicographicallyAgainstLastReadId() {
    var feed = feed("b");

    assertTrue(feed.hasRead("a"));
    assertTrue(feed.hasRead("b"));
    assertFalse(feed.hasRead("c"));
  }

  @Test
  void hasReadIsFalseWhenNothingHasBeenRead() {
    assertFalse(feed(null).hasRead("a"));
  }

  @Test
  void hasReadThrowsOnNullNewsId() {
    // Characterization of defect #3: newsId is declared @Nullable but dereferenced.
    // Phase 1 should make this return false instead of throwing.
    assertThrows(NullPointerException.class, () -> feed("b").hasRead(null));
  }
}
