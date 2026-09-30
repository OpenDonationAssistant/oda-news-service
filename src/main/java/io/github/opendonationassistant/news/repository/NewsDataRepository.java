package io.github.opendonationassistant.news.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface NewsDataRepository extends CrudRepository<NewsData, String> {
  public Page<NewsData> findAllOrderByIdDesc(Pageable pageable);

  @Query(
    value = "SELECT * FROM news ORDER BY id DESC LIMIT 1",
    nativeQuery = true
  )
  public Optional<NewsData> findLast();

  @Query(
    value = "SELECT * FROM news WHERE id > :id ORDER BY id ASC LIMIT 1",
    nativeQuery = true
  )
  public Optional<NewsData> findNextAfter(String id);
}
