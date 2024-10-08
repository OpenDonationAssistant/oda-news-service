package io.github.opendonationassistant.news;

import io.github.opendonationassistant.news.repository.NewsData;
import io.github.opendonationassistant.news.repository.NewsDataRepository;
import io.github.opendonationassistant.news.view.NewsDto;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class News {

  private final @Nonnull String id;
  private final @Nonnull String title;
  private final @Nonnull String description;
  private final @Nonnull String date;
  private final @Nullable String demoUrl;
  private final @Nonnull NewsDataRepository repository;

  public News(
    final @Nonnull String id,
    final @Nonnull String title,
    final @Nonnull String description,
    final @Nonnull String date,
    final @Nullable String demoUrl,
    final @Nonnull NewsDataRepository repository
  ) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.date = date;
    this.demoUrl = demoUrl;
    this.repository = repository;
  }

  public NewsDto asDto() {
    final var dto = new NewsDto();
    dto.setId(id);
    dto.setTitle(title);
    dto.setDescription(description);
    dto.setDemoUrl(demoUrl);
    dto.setDate(date);
    return dto;
  }

  public void save() {
    NewsData data = new NewsData(id, title, description, date, demoUrl);
    repository.save(data);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result =
      prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((demoUrl == null) ? 0 : demoUrl.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    News other = (News) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    if (title == null) {
      if (other.title != null) return false;
    } else if (!title.equals(other.title)) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (demoUrl == null) {
      if (other.demoUrl != null) return false;
    } else if (!demoUrl.equals(other.demoUrl)) return false;
    return true;
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return (
      "{\"_type\"=\"News\",\"id\"=\"" +
      id +
      "\", title\"=\"" +
      title +
      "\", description\"=\"" +
      description +
      "\", date\"=\"" +
      date +
      "\", demoUrl\"=\"" +
      demoUrl +
      "}"
    );
  }
}
