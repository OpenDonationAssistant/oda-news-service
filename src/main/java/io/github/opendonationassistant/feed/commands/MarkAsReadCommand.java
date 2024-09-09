package io.github.opendonationassistant.feed.commands;

import io.github.opendonationassistant.feed.repository.StreamerFeedRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class MarkAsReadCommand {

  private String newsId;

  public String getNewsId() {
    return newsId;
  }

  public void setNewsId(String newsId) {
    this.newsId = newsId;
  }

  public void executeWith(
    Authentication auth,
    StreamerFeedRepository repository
  ) {
    repository.get(getOwnerId(auth)).markAsRead(newsId);
  }

  private String getOwnerId(Authentication auth) {
    return String.valueOf(
      auth.getAttributes().getOrDefault("preferred_username", "")
    );
  }
}
