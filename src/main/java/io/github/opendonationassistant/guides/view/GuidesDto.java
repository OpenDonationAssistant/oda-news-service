package io.github.opendonationassistant.guides.view;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record GuidesDto(List<String> ids) {}