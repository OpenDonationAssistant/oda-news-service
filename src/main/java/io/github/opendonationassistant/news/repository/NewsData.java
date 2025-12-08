package io.github.opendonationassistant.news.repository;

import io.github.opendonationassistant.news.News;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Serdeable
@MappedEntity("news")
public class NewsData {

  @Id
  private @NonNull String id;

  private @NonNull String title;
  private @NonNull String description;
  private @NonNull String date;
  private @Nullable String demoUrl;

  public @NonNull News asNews(@NonNull NewsDataRepository repository) {
    return new News(id, title, description, date, demoUrl, repository);
  }

  public @NonNull String getDate() {
    return date;
  }

  public void setDate(@NonNull String date) {
    this.date = date;
  }

  public @NonNull String getId() {
    return id;
  }

  public void setId(@NonNull String id) {
    this.id = id;
  }

  public @NonNull String getTitle() {
    return title;
  }

  public void setTitle(@NonNull String title) {
    this.title = title;
  }

  public @NonNull String getDescription() {
    return description;
  }

  public void setDescription(@NonNull String description) {
    this.description = description;
  }

  public @Nullable String getDemoUrl() {
    return demoUrl;
  }

  public void setDemoUrl(@Nullable String demoUrl) {
    this.demoUrl = demoUrl;
  }

  @Override
  public @NonNull String toString() {
    return (
      "{\"_type\"=\"NewsData\",\"id\"=\"" +
      id +
      "\", title\"=\"" +
      title +
      "\", description\"=\"" +
      description +
      "\", demoUrl\"=\"" +
      demoUrl +
      "}"
    );
  }

  public NewsData(
    @NonNull String id,
    @NonNull String title,
    @NonNull String description,
    @NonNull String date,
    @Nullable String demoUrl
  ) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.date = date;
    this.demoUrl = demoUrl;
  }
}
