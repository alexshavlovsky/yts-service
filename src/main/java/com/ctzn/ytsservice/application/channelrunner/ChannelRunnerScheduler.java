package com.ctzn.ytsservice.application.channelrunner;

import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@EnableAsync
@Log
@Profile("!disableScheduler")
public class ChannelRunnerScheduler {

    private final ChannelWorkerBinderTransactionWrapper programmaticTransactionService;

    public ChannelRunnerScheduler(ChannelWorkerBinderTransactionWrapper programmaticTransactionService) {
        this.programmaticTransactionService = programmaticTransactionService;
    }

    @Async
    @Scheduled(fixedRate = 10 * 1000)
    public void worker() {
        programmaticTransactionService.bindPendingChannelToWorker((int) Thread.currentThread().getId());
    }

}
