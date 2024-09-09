package io.github.opendonationassistant.feed.repository;

import io.github.opendonationassistant.feed.StreamerFeed;
import io.github.opendonationassistant.news.repository.NewsRepository;
import jakarta.inject.Singleton;

@Singleton
public class StreamerFeedRepository {

  private final StreamerFeedDataRepository repository;
  private final NewsRepository news;

  public StreamerFeedRepository(
    StreamerFeedDataRepository repository,
    NewsRepository news
  ) {
    this.repository = repository;
    this.news = news;
  }

  public StreamerFeed get(String streamerId) {
    return repository
      .findById(streamerId)
      .map(data -> data.asStreamerFeed(repository, news))
      .orElseGet(() -> {
        repository.save(new StreamerFeedData(streamerId, null));
        return new StreamerFeed(streamerId, null, repository, news);
      });
  }
}
