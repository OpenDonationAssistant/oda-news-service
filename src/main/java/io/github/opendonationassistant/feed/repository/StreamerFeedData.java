package io.github.opendonationassistant.feed.repository;

import io.github.opendonationassistant.feed.StreamerFeed;
import io.github.opendonationassistant.news.repository.NewsRepository;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Serdeable
@MappedEntity("feed")
public class StreamerFeedData {

  @Id
  private final @Nonnull String streamerId;

  private final @Nullable String lastReadNewsId;

  public StreamerFeedData(
    @Nonnull String streamerId,
    @Nullable String lastReadNewsId
  ) {
    this.streamerId = streamerId;
    this.lastReadNewsId = lastReadNewsId;
  }

  public @Nonnull StreamerFeed asStreamerFeed(
    StreamerFeedDataRepository repository,
    NewsRepository news
  ) {
    return new StreamerFeed(streamerId, lastReadNewsId, repository, news);
  }

  public @Nullable String getLastReadNewsId() {
    return lastReadNewsId;
  }

  public String getStreamerId() {
    return streamerId;
  }
}
