package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.PersistenceChannelRunner;
import com.ctzn.youtubescraper.core.persistence.PersistenceService;
import org.springframework.stereotype.Component;

@Component
public class ChannelRunnerFactory {

    private final PersistenceService persistenceService;

    public ChannelRunnerFactory(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public PersistenceChannelRunner newRunner(String channelId) {
        return PersistenceChannelRunner.newBuilder(channelId, persistenceService).defaultExecutor().processAllChannelComments().build();
    }

}
