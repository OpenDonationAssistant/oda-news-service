package io.github.opendonationassistant.feed;

import io.github.opendonationassistant.feed.repository.StreamerFeedData;
import io.github.opendonationassistant.feed.repository.StreamerFeedDataRepository;
import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.repository.NewsRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamerFeed {

  private final Logger log = LoggerFactory.getLogger(StreamerFeed.class);
  private final @Nonnull String streamerId;
  private final @Nonnull StreamerFeedDataRepository repository;
  private final @Nonnull NewsRepository news;
  private @Nullable String lastReadNewsId;

  public StreamerFeed(
    @Nonnull String streamerId,
    @Nullable String lastReadNewsId,
    @Nonnull StreamerFeedDataRepository repository,
    @Nonnull NewsRepository news
  ) {
    this.streamerId = streamerId;
    this.lastReadNewsId = lastReadNewsId;
    this.repository = repository;
    this.news = news;
  }

  public @Nonnull Optional<News> nextNews() {
    if (
      Objects.equals(lastReadNewsId, news.last().map(News::getId).orElse(null))
    ) {
      return Optional.empty();
    }
    return news.last();
  }

  public void markAsRead(@Nonnull String newsId) {
    Objects.requireNonNull(newsId);
    this.lastReadNewsId = newsId;
    repository.update(new StreamerFeedData(streamerId, newsId));
  }

  public boolean hasRead(@Nullable String newsId) {
    if (lastReadNewsId == null) {
      return false;
    }
    return lastReadNewsId.compareTo(newsId) >= 0;
  }
}
