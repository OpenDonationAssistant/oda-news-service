package io.github.opendonationassistant.feedback.commands;

import io.github.opendonationassistant.feedback.NewsFeedback;
import io.github.opendonationassistant.feedback.repository.NewsFeedbackRepository;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Serdeable
public class CreateFeedbackCommand {

  @Min(-1)
  @Max(10)
  private int rating;

  public CreateFeedbackCommand(int rating){
    this.rating = rating;
  }

  public NewsFeedback executeWith(
    String newsId,
    String streamerId,
    NewsFeedbackRepository feedbackRepository
  ) {
    return feedbackRepository.create(newsId, streamerId, rating);
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }
}
