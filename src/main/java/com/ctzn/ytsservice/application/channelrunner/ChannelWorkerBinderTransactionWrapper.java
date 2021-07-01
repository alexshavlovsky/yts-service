package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.PersistenceRunnerStepBuilder;
import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.ChannelRunnerConfigDTO;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class ChannelWorkerBinderTransactionWrapper {

    private final ChannelService channelService;
    private final RunnerFactory commentRunnerFactory;
    private final ObjectAssembler mapper;

    public ChannelWorkerBinderTransactionWrapper(ChannelService channelService, RunnerFactory commentRunnerFactory, ObjectAssembler mapper) {
        this.channelService = channelService;
        this.commentRunnerFactory = commentRunnerFactory;
        this.mapper = mapper;
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
        try {
            if (emulationOnly) {
                System.out.println("Emulation: Channel passed to worker: " + channelId);
                Thread.sleep(sleepDuration);
                System.out.println("Emulation: Unlock channel: " + channelId);
            } else {
                ChannelRunnerConfigDTO runnerConfig = mapper.parse(channel.getContextStatus().getStatusMessage(), ChannelRunnerConfigDTO.class);
                PersistenceRunnerStepBuilder.BuildStep builder = commentRunnerFactory.newChannelRunnerBuilder(runnerConfig);
                log.info("Pending channel passed to worker: [config: {}, workerId: {}]", builder.toString(), workerId);
                builder.build().call();
            }
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
