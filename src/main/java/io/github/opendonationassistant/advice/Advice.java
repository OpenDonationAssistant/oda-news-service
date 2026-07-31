package io.github.opendonationassistant.advice;

import io.github.opendonationassistant.advice.repository.AdviceDataRepository;
import io.github.opendonationassistant.advice.view.AdviceDto;
import org.jspecify.annotations.NonNull;

public class Advice {

  private final @NonNull String id;
  private final @NonNull String text;
  private final @NonNull AdviceDataRepository repository;

  public Advice(
    final @NonNull String id,
    final @NonNull String text,
    final @NonNull AdviceDataRepository repository
  ) {
    this.id = id;
    this.text = text;
    this.repository = repository;
  }

  public AdviceDto asDto() {
    return new AdviceDto(id, text);
  }

  public @NonNull String getId() {
    return id;
  }

  public @NonNull String getText() {
    return text;
  }
}
