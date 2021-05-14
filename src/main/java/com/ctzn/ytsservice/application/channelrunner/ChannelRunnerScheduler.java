package com.ctzn.ytsservice.application.channelrunner;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@EnableAsync
@Slf4j
@Profile("!disableScheduler")
public class ChannelRunnerScheduler {

    private final ChannelWorkerBinderTransactionWrapper channelWorkerBinderTransactionWrapper;
    private final VideoListWorkerBinderTransactionWrapper videoListWorkerBinderTransactionWrapper;

    public ChannelRunnerScheduler(ChannelWorkerBinderTransactionWrapper channelWorkerBinderTransactionWrapper, VideoListWorkerBinderTransactionWrapper videoListWorkerBinderTransactionWrapper) {
        this.channelWorkerBinderTransactionWrapper = channelWorkerBinderTransactionWrapper;
        this.videoListWorkerBinderTransactionWrapper = videoListWorkerBinderTransactionWrapper;
    }

    @Async
    @Scheduled(fixedRate = 10 * 1000)
    public void worker() {
        channelWorkerBinderTransactionWrapper.bindPendingChannelToWorker((int) Thread.currentThread().getId());
        videoListWorkerBinderTransactionWrapper.bindVideoToWorker((int) Thread.currentThread().getId());
    }

}
