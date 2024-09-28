package io.github.opendonationassistant.feedback.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.feedback.NewsFeedback;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nonnull;

@Serdeable
@MappedEntity("feedback")
public class NewsFeedbackData {

  @Id
  private final @Nonnull String id;

  private final String newsId;
  private final String streamerId;
  private final int rating;

  public NewsFeedbackData(String id, String newsId, String streamerId, int rating) {
    this.id = id;
    this.newsId = newsId;
    this.streamerId = streamerId;
    this.rating = rating;
  }

  public NewsFeedbackData(String newsId, String streamerId, int rating) {
    this(Generators.timeBasedGenerator().generate().toString(), newsId, streamerId, rating);
  }

  public NewsFeedback asNewsFeedback() {
    return new NewsFeedback(id, newsId, streamerId, rating);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((newsId == null) ? 0 : newsId.hashCode());
    result = prime * result + ((streamerId == null) ? 0 : streamerId.hashCode());
    result = prime * result + rating;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NewsFeedbackData other = (NewsFeedbackData) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (newsId == null) {
      if (other.newsId != null)
        return false;
    } else if (!newsId.equals(other.newsId))
      return false;
    if (streamerId == null) {
      if (other.streamerId != null)
        return false;
    } else if (!streamerId.equals(other.streamerId))
      return false;
    if (rating != other.rating)
      return false;
    return true;
  }

  public String getId() {
    return id;
  }

  public String getNewsId() {
    return newsId;
  }

  public String getStreamerId() {
    return streamerId;
  }

  public int getRating() {
    return rating;
  }

}
