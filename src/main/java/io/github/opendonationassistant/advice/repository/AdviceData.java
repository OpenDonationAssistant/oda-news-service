package io.github.opendonationassistant.advice.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("advices")
public record AdviceData(@Id String id, String text) {}
