package io.github.opendonationassistant.advice.view;

import io.github.opendonationassistant.advice.repository.AdviceData;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record AdviceDto(String id, String text) {
  public static AdviceDto from(AdviceData advice) {
    return new AdviceDto(advice.id(), advice.text());
  }
}
