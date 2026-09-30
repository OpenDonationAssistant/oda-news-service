package io.github.opendonationassistant.feedback.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.feedback.NewsFeedback;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class NewsFeedbackRepository {

  private final NewsFeedbackDataRepository dataRepository;

  @Inject
  public NewsFeedbackRepository(NewsFeedbackDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  public NewsFeedback create(String newsId, String streamerId, int rating) {
    var id = Generators.timeBasedEpochGenerator().generate().toString();
    NewsFeedbackData data = new NewsFeedbackData(id, newsId, streamerId, rating);
    dataRepository.save(data);
    return data.asNewsFeedback();
  }
}
