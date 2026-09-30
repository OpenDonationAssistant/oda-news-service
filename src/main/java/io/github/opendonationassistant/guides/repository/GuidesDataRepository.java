package io.github.opendonationassistant.guides.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GuidesDataRepository extends CrudRepository<GuidesData, String> {
  @Query(
    value = "INSERT INTO guides (recipient_id, ids) " +
    "VALUES (:recipientId, jsonb_build_array(:guideId)) " +
    "ON CONFLICT (recipient_id) DO UPDATE " +
    "SET ids = COALESCE(guides.ids, '[]'::jsonb) || jsonb_build_array(:guideId) " +
    "WHERE NOT (COALESCE(guides.ids, '[]'::jsonb) @> jsonb_build_array(:guideId))",
    nativeQuery = true
  )
  void markRead(String recipientId, String guideId);
}
