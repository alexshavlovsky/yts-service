package com.ctzn.ytsservice.domain;

import com.ctzn.youtubescraper.persistence.PersistenceChannelRunner;
import org.springframework.stereotype.Component;

@Component
public class ChannelRunnerFactory {

    private PersistenceService persistenceService;

    public ChannelRunnerFactory(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public PersistenceChannelRunner newRunner(String channelId) {
        return PersistenceChannelRunner.newBuilder(channelId, persistenceService).defaultExecutor().processAllChannelComments().build();
    }

}
