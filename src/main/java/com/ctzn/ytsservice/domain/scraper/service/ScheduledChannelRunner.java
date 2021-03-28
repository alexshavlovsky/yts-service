package com.ctzn.ytsservice.domain.scraper.service;

import com.ctzn.ytsservice.domain.scraper.entity.ChannelEntity;
import com.ctzn.ytsservice.domain.scraper.entity.ChannelStatus;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@EnableScheduling
public class ScheduledChannelRunner {

    private final ChannelRunnerFactory commentRunnerFactory;
    private final ChannelRepository channelRepository;

    public ScheduledChannelRunner(ChannelRunnerFactory commentRunnerFactory, ChannelRepository channelRepository) {
        this.commentRunnerFactory = commentRunnerFactory;
        this.channelRepository = channelRepository;
    }

    private void setChannelStatus(String channelId, ChannelStatus channelStatus) {
        Optional<ChannelEntity> optionalChannel = channelRepository.findById(channelId);
        if (optionalChannel.isEmpty()) return;
        ChannelEntity channel = optionalChannel.get();
        channel.setChannelStatus(channelStatus);
        channelRepository.save(channel);
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void newTask() {
        List<ChannelEntity> channels = channelRepository.findAllByChannelStatusOrderByCreatedDate(ChannelStatus.PENDING);
        if (channels.isEmpty()) return;
        ChannelEntity channelEntity = channels.get(0);
        String id = channelEntity.getChannelId();
        try {
            commentRunnerFactory.newRunner(id).call();
            setChannelStatus(id, ChannelStatus.DONE);
        } catch (Exception e) {
            setChannelStatus(id, ChannelStatus.ERROR);
        }
    }

}
