package io.github.opendonationassistant.warning.repository;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record WarningData(String message) {}
