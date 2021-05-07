package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.PersistenceRunner;
import com.ctzn.youtubescraper.core.persistence.PersistenceService;
import org.springframework.stereotype.Component;

@Component
public class ChannelRunnerFactory {

    private final PersistenceService persistenceService;

    public ChannelRunnerFactory(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public PersistenceRunner newRunner(String channelId) {
        return PersistenceRunner.newChannelRunnerBuilder(channelId, persistenceService).defaultExecutor().processAllComments().build();
    }

}
