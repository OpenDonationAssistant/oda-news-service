package io.github.opendonationassistant.advice.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.advice.Advice;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class AdviceRepository {

  private final AdviceDataRepository adviceDataRepository;

  @Inject
  public AdviceRepository(AdviceDataRepository adviceDataRepository) {
    this.adviceDataRepository = adviceDataRepository;
  }

  public Advice create(String text) {
    var id = Generators.timeBasedEpochGenerator().generate().toString();
    AdviceData data = new AdviceData(id, text);
    adviceDataRepository.save(data);
    return data.asAdvice(adviceDataRepository);
  }
}
