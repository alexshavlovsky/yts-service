package com.ctzn.ytsservice.domain.channelrunner;

import com.ctzn.youtubescraper.persistence.PersistenceChannelRunner;
import com.ctzn.youtubescraper.persistence.PersistenceService;
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
