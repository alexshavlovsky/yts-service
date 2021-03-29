package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.persistence.dto.StatusCode;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
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
    private final ChannelRepository channelRepository;

    public ChannelRunnerScheduler(ChannelRunnerFactory commentRunnerFactory, ChannelRepository channelRepository) {
        this.commentRunnerFactory = commentRunnerFactory;
        this.channelRepository = channelRepository;
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void newTask() {
        List<ChannelEntity> channels = channelRepository.findAllByContextStatus_StatusCodeOrderByCreatedDate(StatusCode.PENDING);
        if (channels.isEmpty()) return;
        ChannelEntity channelEntity = channels.get(0);
        String id = channelEntity.getChannelId();
        try {
            commentRunnerFactory.newRunner(id).call();
        } catch (Exception e) {
            log.severe(e::getMessage);
        }
    }

}
