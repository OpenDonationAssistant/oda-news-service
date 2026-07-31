package io.github.opendonationassistant.advice.repository;

import io.github.opendonationassistant.advice.Advice;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import org.jspecify.annotations.NonNull;

@Serdeable
@MappedEntity("advices")
public class AdviceData {

  @Id
  private @NonNull String id;

  private @NonNull String text;

  public @NonNull Advice asAdvice(@NonNull AdviceDataRepository repository) {
    return new Advice(id, text, repository);
  }

  public @NonNull String getId() {
    return id;
  }

  public void setId(@NonNull String id) {
    this.id = id;
  }

  public @NonNull String getText() {
    return text;
  }

  public void setText(@NonNull String text) {
    this.text = text;
  }

  @Override
  public @NonNull String toString() {
    return (
      "{\"_type\"=\"AdviceData\",\"id\"=\"" +
      id +
      "\", text\"=\"" +
      text +
      "\"}"
    );
  }

  public AdviceData(@NonNull String id, @NonNull String text) {
    this.id = id;
    this.text = text;
  }
}
