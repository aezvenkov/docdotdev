package com.example.clientbackend.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

import static com.example.clientbackend.Constants.USERS_COLLECTION;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        Cache usersCache = new ConcurrentMapCache(USERS_COLLECTION);
        cacheManager.setCaches(Collections.singleton(usersCache));

        return cacheManager;
    }
}
