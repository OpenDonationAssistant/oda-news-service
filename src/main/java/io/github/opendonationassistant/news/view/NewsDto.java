package io.github.opendonationassistant.news.view;

import org.jspecify.annotations.Nullable;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record NewsDto(
  String id,
  String title,
  String description,
  String date,
  @Nullable String demoUrl
) {}
