package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.ytsservice.application.service.VideoService;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VideoListWorkerBinderTransactionWrapper {

    private final VideoService videoService;
    private final RunnerFactory commentRunnerFactory;

    public VideoListWorkerBinderTransactionWrapper(VideoService videoService, RunnerFactory commentRunnerFactory) {
        this.videoService = videoService;
        this.commentRunnerFactory = commentRunnerFactory;
    }

    void bindVideoToWorker(Integer workerId) {
        if (!videoService.existOnePendingVideo()) return;
        List<VideoEntity> videos = null;
        int retryCount = 0;
        do {
            try {
                videos = videoService.lockPendingVideos(workerId);
            } catch (Exception e) {
                try {
                    Thread.sleep(retryCount * 100 + new Random().nextInt(100));
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (++retryCount < 3 && videos == null);
        if (videos == null) return;
        List<String> videosIds = videos.stream().map(videoEntity -> videoEntity.getNaturalId().getVideoId()).collect(Collectors.toList());
        List<Long> ids = videos.stream().map(VideoEntity::getId).collect(Collectors.toList());
        log.info("Videos passed to worker: [videoCount: {}, workerId: {}]", videosIds.size(), workerId);
        try {
            commentRunnerFactory.newVideoListRunner(videosIds).call();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            int unlockedCount = videoService.unlockVideos(ids, workerId);
            if (unlockedCount != ids.size())
                log.warn("Error while unlocking video: [videoCount: {}, errorCount: {}, workerId: {}]", ids.size(), ids.size() - unlockedCount, workerId);
        }
    }

}
