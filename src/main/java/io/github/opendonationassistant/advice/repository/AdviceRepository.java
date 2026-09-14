package io.github.opendonationassistant.advice.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.advice.Advice;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;

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
    return convert(data);
  }

  public Optional<Advice> getRandom() {
    return adviceDataRepository.findRandom().map(this::convert);
  }

  private Advice convert(AdviceData data) {
    return new Advice(data, adviceDataRepository);
  }
}
