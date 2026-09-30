package io.github.opendonationassistant.guides;

import io.github.opendonationassistant.guides.repository.GuidesData;
import io.github.opendonationassistant.guides.repository.GuidesDataRepository;
import io.github.opendonationassistant.guides.view.GuidesDto;
import java.util.List;
import org.jspecify.annotations.NonNull;

public class Guides {

  private final @NonNull GuidesData data;
  private final @NonNull GuidesDataRepository repository;

  public Guides(
    final @NonNull GuidesData data,
    final @NonNull GuidesDataRepository repository
  ) {
    this.data = data;
    this.repository = repository;
  }

  public @NonNull GuidesData data() {
    return data;
  }

  public @NonNull GuidesDto asDto() {
    return new GuidesDto(List.copyOf(data.getIds()));
  }

  public void markRead(@NonNull String guideId) {
    repository.markRead(data.getRecipientId(), guideId);
  }
}