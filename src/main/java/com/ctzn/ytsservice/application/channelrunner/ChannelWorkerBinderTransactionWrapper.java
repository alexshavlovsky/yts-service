package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class ChannelWorkerBinderTransactionWrapper {

    private final ChannelService channelService;
    private final RunnerFactory commentRunnerFactory;

    public ChannelWorkerBinderTransactionWrapper(ChannelService channelService, RunnerFactory commentRunnerFactory) {
        this.channelService = channelService;
        this.commentRunnerFactory = commentRunnerFactory;
    }

    void bindPendingChannelToWorker(Integer workerId, boolean emulationOnly, int sleepDuration) {
        if (!channelService.existOnePendingChannel()) return;
        ChannelEntity channel = null;
        int retryCount = 0;
        do {
            try {
                channel = channelService.lockOnePendingChannel(workerId);
            } catch (Exception e) {
                try {
                    Thread.sleep(retryCount * 100 + new Random().nextInt(100));
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (++retryCount < 3 && channel == null);
        if (channel == null) return;
        String channelId = channel.getNaturalId().getChannelId();
        log.info("Pending channel passed to worker: [channelId: {}, workerId: {}]", channelId, workerId);
        try {
            if (emulationOnly) {
                System.out.println("Emulation: Channel passed to worker: " + channelId);
                Thread.sleep(sleepDuration);
                System.out.println("Emulation: Unlock channel: " + channelId);
            } else commentRunnerFactory.newChannelRunner(channelId).call();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (!channelService.unlockChannel(channel.getId(), workerId))
                log.warn("Error while unlocking channel [channelId: {}, workerId: {}]", channelId, workerId);
        }
    }

    public void bindPendingChannelToWorker(Integer workerId) {
        bindPendingChannelToWorker(workerId, false, 0);
    }

}
