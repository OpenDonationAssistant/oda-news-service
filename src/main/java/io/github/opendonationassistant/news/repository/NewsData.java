package io.github.opendonationassistant.news.repository;

import io.github.opendonationassistant.news.News;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Serdeable
@MappedEntity("news")
public class NewsData {

  @Id
  private @Nonnull String id;

  private @Nonnull String title;
  private @Nonnull String description;
  private @Nonnull String date;
  private @Nullable String demoUrl;

  public @Nonnull News asNews(@Nonnull NewsDataRepository repository) {
    return new News(id, title, description, date, demoUrl, repository);
  }

  public @Nonnull String getDate() {
    return date;
  }

  public void setDate(@Nonnull String date) {
    this.date = date;
  }

  public @Nonnull String getId() {
    return id;
  }

  public void setId(@Nonnull String id) {
    this.id = id;
  }

  public @Nonnull String getTitle() {
    return title;
  }

  public void setTitle(@Nonnull String title) {
    this.title = title;
  }

  public @Nonnull String getDescription() {
    return description;
  }

  public void setDescription(@Nonnull String description) {
    this.description = description;
  }

  public @Nullable String getDemoUrl() {
    return demoUrl;
  }

  public void setDemoUrl(@Nullable String demoUrl) {
    this.demoUrl = demoUrl;
  }

  @Override
  public @Nonnull String toString() {
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
    @Nonnull String id,
    @Nonnull String title,
    @Nonnull String description,
    @Nonnull String date,
    @Nullable String demoUrl
  ) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.date = date;
    this.demoUrl = demoUrl;
  }
}
