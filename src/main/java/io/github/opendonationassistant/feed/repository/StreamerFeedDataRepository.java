package io.github.opendonationassistant.feed.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface StreamerFeedDataRepository
  extends CrudRepository<StreamerFeedData, String> {

  @Query(
    value = "INSERT INTO feed (streamer_id, last_read_news_id) " +
    "VALUES (:streamerId, :newsId) " +
    "ON CONFLICT (streamer_id) DO UPDATE SET last_read_news_id = :newsId",
    nativeQuery = true
  )
  void upsert(String streamerId, String newsId);
}
