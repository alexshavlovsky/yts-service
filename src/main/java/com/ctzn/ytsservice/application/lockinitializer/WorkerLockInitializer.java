package com.ctzn.ytsservice.application.lockinitializer;


import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.application.ftsinitializer.FtsInitializer;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.domain.entities.WorkerLogEntity;
import com.ctzn.ytsservice.infrastructure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastructure.repositories.VideoRepository;
import com.ctzn.ytsservice.infrastructure.repositories.WorkerLogRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class WorkerLockInitializer {

    // this is necessary to ensure the order of execution
    private FtsInitializer ftsInitializer;

    private ChannelRepository channelRepository;
    private VideoRepository videoRepository;
    private WorkerLogRepository workerLogRepository;

    public WorkerLockInitializer(FtsInitializer ftsInitializer, ChannelRepository channelRepository, VideoRepository videoRepository, WorkerLogRepository workerLogRepository) {
        this.ftsInitializer = ftsInitializer;
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.workerLogRepository = workerLogRepository;
    }

    @PostConstruct
    public void unlockWorkerLocks() {
        channelRepository.resetLocks();
        List<ChannelEntity> channelEntityList = channelRepository.findAllByContextStatus_statusCode(StatusCode.PASSED_TO_WORKER);
        channelEntityList.forEach(e -> {
            e.getContextStatus().setStatusCode(StatusCode.ERROR);
            e.getContextStatus().setStatusMessage("Worker was aborted unexpectedly");
            workerLogRepository.save(new WorkerLogEntity(null, e.getChannelId(), e.getContextStatus()));
        });
        channelRepository.saveAll(channelEntityList);
        videoRepository.resetLocks();
        List<VideoEntity> videoEntityList = videoRepository.findAllByContextStatus_statusCode(StatusCode.PASSED_TO_WORKER);
        videoEntityList.forEach(e -> {
            e.getContextStatus().setStatusCode(StatusCode.ERROR);
            e.getContextStatus().setStatusMessage("Worker was aborted unexpectedly");
            workerLogRepository.save(new WorkerLogEntity(null, e.getVideoId(), e.getContextStatus()));
        });
        videoRepository.saveAll(videoEntityList);
    }

}
