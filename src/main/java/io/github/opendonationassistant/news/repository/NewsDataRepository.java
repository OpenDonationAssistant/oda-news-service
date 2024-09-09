package io.github.opendonationassistant.news.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface NewsDataRepository extends CrudRepository<NewsData, String> {
  public Page<NewsData> findAllOrderByIdDesc(Pageable pageable);
}
