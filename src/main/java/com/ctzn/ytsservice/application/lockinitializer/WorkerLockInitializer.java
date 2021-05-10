package com.ctzn.ytsservice.application.lockinitializer;


import com.ctzn.ytsservice.application.ftsinitializer.FtsInitializer;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class WorkerLockInitializer {

    // this is necessary to ensure the order of execution
    private FtsInitializer ftsInitializer;

    private ChannelRepository channelRepository;
    private VideoRepository videoRepository;

    public WorkerLockInitializer(FtsInitializer ftsInitializer, ChannelRepository channelRepository, VideoRepository videoRepository) {
        this.ftsInitializer = ftsInitializer;
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
    }

    @PostConstruct
    public void unlockWorkerLocks() {
        channelRepository.resetLocks();
        videoRepository.resetLocks();
    }

}
