package es.caib.notib.logic.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

@Configuration
@EnableCaching
public class CachingConfig {

//    @Bean
//    public CacheManager cacheManager() {
//        CachingProvider cachingProvider = Caching.getCachingProvider();
//        return cachingProvider.getCacheManager();
//    }

}
