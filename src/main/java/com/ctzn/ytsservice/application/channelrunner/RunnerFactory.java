package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.PersistenceRunner;
import com.ctzn.youtubescraper.core.persistence.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RunnerFactory {

    private final PersistenceService persistenceService;

    public RunnerFactory(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public PersistenceRunner newChannelRunner(String channelId) {
        return PersistenceRunner.newChannelRunnerBuilder(channelId, persistenceService).defaultExecutor().processAllComments().build();
    }

    public PersistenceRunner newVideoListRunner(List<String> videoIds) {
        return PersistenceRunner.newVideoListRunnerBuilder(videoIds, persistenceService).defaultExecutor().processAllComments().build();
    }

}
