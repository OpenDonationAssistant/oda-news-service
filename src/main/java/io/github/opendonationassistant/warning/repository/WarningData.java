package io.github.opendonationassistant.warning.repository;

import io.micronaut.serde.annotation.Serdeable;
import org.jspecify.annotations.Nullable;

@Serdeable
public record WarningData(String message, @Nullable String component) {
  public WarningData(String message) {
    this(message, null);
  }
}
