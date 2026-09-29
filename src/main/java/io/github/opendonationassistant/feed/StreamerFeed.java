package io.github.opendonationassistant.feed;

import io.github.opendonationassistant.feed.repository.StreamerFeedData;
import io.github.opendonationassistant.feed.repository.StreamerFeedDataRepository;
import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.repository.NewsRepository;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class StreamerFeed {

  private final @NonNull String streamerId;
  private final @NonNull StreamerFeedDataRepository repository;
  private final @NonNull NewsRepository news;
  private @Nullable String lastReadNewsId;

  public StreamerFeed(
    @NonNull String streamerId,
    @Nullable String lastReadNewsId,
    @NonNull StreamerFeedDataRepository repository,
    @NonNull NewsRepository news
  ) {
    this.streamerId = streamerId;
    this.lastReadNewsId = lastReadNewsId;
    this.repository = repository;
    this.news = news;
  }

  public @NonNull Optional<News> nextNews() {
    if (
      Objects.equals(lastReadNewsId, news.last().map(News::getId).orElse(null))
    ) {
      return Optional.empty();
    }
    return news.last();
  }

  public void markAsRead(@NonNull String newsId) {
    Objects.requireNonNull(newsId);
    this.lastReadNewsId = newsId;
    repository.update(new StreamerFeedData(streamerId, newsId));
  }

  public boolean hasRead(@Nullable String newsId) {
    if (newsId == null) {
      return true;
    }
    if (lastReadNewsId == null) {
      return false;
    }
    return lastReadNewsId.compareTo(newsId) >= 0;
  }
}
