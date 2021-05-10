package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Log
public class ChannelWorkerBinderTransactionWrapper {

    private final ChannelService channelService;
    private final ChannelRunnerFactory commentRunnerFactory;

    public ChannelWorkerBinderTransactionWrapper(ChannelService channelService, ChannelRunnerFactory commentRunnerFactory) {
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
        log.info(String.format("Pending channel passed to worker: {channelId = %s, workerId = %d}", channelId, workerId));
        try {
            if (emulationOnly) {
                System.out.println("Emulation: Channel passed to worker: " + channelId);
                Thread.sleep(sleepDuration);
                System.out.println("Emulation: Unlock channel: " + channelId);
            } else commentRunnerFactory.newRunner(channelId).call();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e::getMessage);
        } finally {
            if (!channelService.unlockChannel(channel.getId(), workerId))
                log.warning(String.format("Error while unlocking channel {channelId = %s, workerId = %d}", channelId, workerId));
        }
    }

    public void bindPendingChannelToWorker(Integer workerId) {
        bindPendingChannelToWorker(workerId, false, 0);
    }

}
