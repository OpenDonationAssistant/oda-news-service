package io.github.opendonationassistant.news.commands;

import io.github.opendonationassistant.news.News;
import io.github.opendonationassistant.news.repository.NewsRepository;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class AddNewsCommand {

  public String title;
  public String description;
  public String date;
  public String demoUrl;

  public News executeWith(NewsRepository newsRepository) {
    return newsRepository.create(title, description, date, demoUrl);
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
}
