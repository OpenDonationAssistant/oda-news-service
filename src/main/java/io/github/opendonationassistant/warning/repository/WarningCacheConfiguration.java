package io.github.opendonationassistant.warning.repository;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import org.infinispan.commons.api.CacheContainerAdmin.AdminFlag;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;

@Factory
public class WarningCacheConfiguration {

  private static final String CACHE_NAME = "warnings";

  @Singleton
  public Map<String, List<WarningData>> streamelementsCache(
    EmbeddedCacheManager cacheManager
  ) {
    var configuration = new ConfigurationBuilder().simpleCache(true).build();
    return cacheManager
      .administration()
      .withFlags(AdminFlag.VOLATILE)
      .getOrCreateCache(CACHE_NAME, configuration);
  }
}
