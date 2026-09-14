package io.github.opendonationassistant.advice;

import io.github.opendonationassistant.advice.repository.AdviceData;
import io.github.opendonationassistant.advice.repository.AdviceDataRepository;
import org.jspecify.annotations.NonNull;

public class Advice {

  private final @NonNull AdviceData data;
  private final @NonNull AdviceDataRepository repository;

  public Advice(
    final @NonNull AdviceData data,
    final @NonNull AdviceDataRepository repository
  ) {
    this.data = data;
    this.repository = repository;
  }

  public @NonNull AdviceData data() {
    return data;
  }
}
