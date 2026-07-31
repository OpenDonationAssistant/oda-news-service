package io.github.opendonationassistant.advice.view;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record AdviceDto(String id, String text) {}
