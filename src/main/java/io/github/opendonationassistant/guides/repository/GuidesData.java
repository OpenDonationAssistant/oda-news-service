package io.github.opendonationassistant.guides.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import org.jspecify.annotations.NonNull;

@Serdeable
@MappedEntity("guides")
public class GuidesData {

  @Id
  private @NonNull String recipientId;

  @TypeDef(type = DataType.JSON)
  private @NonNull List<String> ids;

  public GuidesData(@NonNull String recipientId, @NonNull List<String> ids) {
    this.recipientId = recipientId;
    this.ids = ids;
  }

  public @NonNull String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(@NonNull String recipientId) {
    this.recipientId = recipientId;
  }

  public @NonNull List<String> getIds() {
    return ids;
  }

  public void setIds(@NonNull List<String> ids) {
    this.ids = ids;
  }
}