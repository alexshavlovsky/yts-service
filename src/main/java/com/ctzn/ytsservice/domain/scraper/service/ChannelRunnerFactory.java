package com.ctzn.ytsservice.domain.scraper.service;

import com.ctzn.youtubescraper.persistence.PersistenceChannelRunner;
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
