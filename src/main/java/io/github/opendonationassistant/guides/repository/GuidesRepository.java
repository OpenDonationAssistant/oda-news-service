package io.github.opendonationassistant.guides.repository;

import io.github.opendonationassistant.guides.Guides;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import org.jspecify.annotations.NonNull;

@Singleton
public class GuidesRepository {

  private final GuidesDataRepository dataRepository;

  @Inject
  public GuidesRepository(GuidesDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  public @NonNull Guides get(@NonNull String recipientId) {
    return dataRepository
      .findById(recipientId)
      .map(data -> new Guides(data, dataRepository))
      .orElseGet(() -> {
        var data = new GuidesData(recipientId, List.of());
        dataRepository.save(data);
        return new Guides(data, dataRepository);
      });
  }
}