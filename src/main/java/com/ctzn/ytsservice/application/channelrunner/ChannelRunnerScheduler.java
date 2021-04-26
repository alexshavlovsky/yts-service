package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Log
public class ChannelRunnerScheduler {

    private final ChannelRunnerFactory commentRunnerFactory;
    private final ChannelService channelService;

    public ChannelRunnerScheduler(ChannelRunnerFactory commentRunnerFactory, ChannelService channelService) {
        this.commentRunnerFactory = commentRunnerFactory;
        this.channelService = channelService;
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void newTask() {
        List<ChannelEntity> channels = channelService.getPendingList();
        if (channels.isEmpty()) return;
        ChannelEntity channelEntity = channels.get(0);
        String id = channelEntity.getNaturalId().getChannelId();
        try {
            commentRunnerFactory.newRunner(id).call();
        } catch (Exception e) {
            log.severe(e::getMessage);
        }
    }

}
