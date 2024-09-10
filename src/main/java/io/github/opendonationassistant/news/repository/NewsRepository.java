package io.github.opendonationassistant.news.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.news.News;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
public class NewsRepository {

  private final NewsDataRepository newsDataRepository;

  @Inject
  public NewsRepository(NewsDataRepository newsDataRepository) {
    this.newsDataRepository = newsDataRepository;
  }

  public Optional<News> get(String id) {
    return newsDataRepository
      .findById(id)
      .map(data -> data.asNews(newsDataRepository));
  }

  public Optional<News> last() {
    return newsDataRepository
      .findAll()
      .stream()
      .sorted((o1, o2) -> -o1.getId().compareTo(o2.getId()))
      .findFirst()
      .map(data -> data.asNews(newsDataRepository));
  }

  public Page<News> list(Pageable pageable) {
    return newsDataRepository
      .findAllOrderByIdDesc(pageable)
      .map(data -> data.asNews(newsDataRepository));
  }

  public Optional<News> nextAfter(String id) {
    return newsDataRepository
      .findAll()
      .stream()
      .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
      .filter(data -> data.getId().compareTo(id) > 0)
      .findFirst()
      .map(data -> data.asNews(newsDataRepository));
  }

  public News create(
    String title,
    String description,
    String date,
    String demoUrl
  ) {
    var id = Generators.timeBasedEpochGenerator().generate().toString();
    NewsData newsData = new NewsData(id, title, description, date, demoUrl);
    newsDataRepository.save(newsData);
    return newsData.asNews(newsDataRepository);
  }
}
