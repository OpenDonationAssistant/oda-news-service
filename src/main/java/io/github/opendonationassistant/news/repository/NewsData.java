package io.github.opendonationassistant.news.repository;

import io.github.opendonationassistant.news.News;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("news")
public class NewsData {

  @Id
  private String id;

  private String title;
  private String description;
  private String date;
  private String demoUrl;

  public News asNews(NewsDataRepository repository) {
    return new News(id, title, description, date, demoUrl, repository);
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

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

  @Override
  public String toString() {
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

  public NewsData(){}

  public NewsData(String id, String title, String description, String demoUrl) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.demoUrl = demoUrl;
  }
}
