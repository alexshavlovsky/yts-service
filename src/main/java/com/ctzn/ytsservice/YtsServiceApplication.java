package com.ctzn.ytsservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class YtsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtsServiceApplication.class, args);
    }

    @Autowired
    CacheManager cacheManager;

    @Scheduled(fixedRate = 120000)
    public void evictAllCachesAtIntervals() {
        cacheManager.getCacheNames().stream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

}
