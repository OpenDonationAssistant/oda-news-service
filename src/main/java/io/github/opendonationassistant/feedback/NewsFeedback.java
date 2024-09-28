package io.github.opendonationassistant.feedback;

public class NewsFeedback {
  private final String id;
  private final String newsId;
  private final String streamerId;
  private int rating;

  public NewsFeedback(String id, String newsId, String streamerId, int rating) {
    this.id = id;
    this.newsId = newsId;
    this.streamerId = streamerId;
    this.rating = rating;
  }
}
