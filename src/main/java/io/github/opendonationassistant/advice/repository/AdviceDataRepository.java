package io.github.opendonationassistant.advice.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AdviceDataRepository extends CrudRepository<AdviceData, String> {

  @Query(value = "SELECT * FROM advices ORDER BY random() LIMIT 1", nativeQuery = true)
  Optional<AdviceData> findRandom();
}
