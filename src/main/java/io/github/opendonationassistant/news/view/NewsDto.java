package io.github.opendonationassistant.news.view;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class NewsDto {

  private String id;
  private String title;
  private String description;
  private String date;
  private String demoUrl;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDemoUrl() {
    return demoUrl;
  }

  public void setDemoUrl(String demoUrl) {
    this.demoUrl = demoUrl;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
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
    NewsDto other = (NewsDto) obj;
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

  @Override
  public String toString() {
    return (
      "{\"_type\"=\"NewsDto\",\"id\"=\"" +
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
}
