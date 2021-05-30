package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.PersistenceRunner;
import com.ctzn.youtubescraper.core.persistence.PersistenceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class RunnerFactory {

    @Value("${app.scraper.runner.thread.number:10}")
    Integer numberOfThreads;

    private final PersistenceService persistenceService;

    public RunnerFactory(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public PersistenceRunner newChannelRunner(String channelId) {
        return PersistenceRunner.newChannelRunnerBuilder(channelId, persistenceService)
                .withExecutor(numberOfThreads, Duration.ofHours(1)).processAllComments().build();
    }

    public PersistenceRunner newVideoListRunner(List<String> videoIds) {
        return PersistenceRunner.newVideoListRunnerBuilder(videoIds, persistenceService)
                .withExecutor(numberOfThreads, Duration.ofHours(1)).processAllComments().build();
    }

}
