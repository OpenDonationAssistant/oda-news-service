package io.github.opendonationassistant.warning.repository;

import io.micronaut.serde.annotation.Serdeable;
import org.jspecify.annotations.Nullable;

@Serdeable
public record WarningData(
  String message,
  @Nullable String component,
  long timestamp
) {
  public WarningData(String message, @Nullable String component) {
    this(message, component, System.currentTimeMillis());
  }

  public WarningData(String message) {
    this(message, null);
  }
}
