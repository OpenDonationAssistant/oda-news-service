package io.github.opendonationassistant.feedback.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface NewsFeedbackDataRepository
  extends CrudRepository<NewsFeedbackData, String> {}
