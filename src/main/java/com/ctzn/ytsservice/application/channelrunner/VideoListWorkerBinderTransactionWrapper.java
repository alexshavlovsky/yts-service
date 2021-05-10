package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.ytsservice.application.service.VideoService;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Log
public class VideoListWorkerBinderTransactionWrapper {

    private final VideoService videoService;
    private final RunnerFactory commentRunnerFactory;

    public VideoListWorkerBinderTransactionWrapper(VideoService videoService, RunnerFactory commentRunnerFactory) {
        this.videoService = videoService;
        this.commentRunnerFactory = commentRunnerFactory;
    }

    void bindVideoToWorker(Integer workerId) {
        if (!videoService.existOnePendingVideo()) return;
        VideoEntity video = null;
        int retryCount = 0;
        do {
            try {
                video = videoService.lockOnePendingVideo(workerId);
            } catch (Exception e) {
                try {
                    Thread.sleep(retryCount * 100 + new Random().nextInt(100));
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (++retryCount < 3 && video == null);
        if (video == null) return;
        String videoId = video.getNaturalId().getVideoId();
        log.info(String.format("Video passed to worker: {videoId = %s, workerId = %d}", videoId, workerId));
        try {
            commentRunnerFactory.newVideoListRunner(List.of(videoId)).call();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e::getMessage);
        } finally {
            if (!videoService.unlockVideo(video.getId(), workerId))
                log.warning(String.format("Error while unlocking video {videoId = %s, workerId = %d}", videoId, workerId));
        }
    }

}
