package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"h2db", "h2mem", "disableScheduler"})
@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:${random.uuid}"
})
class ChannelRunnerSchedulerConcurrentTest {

    @Autowired
    ChannelService channelService;

    @Autowired
    ChannelWorkerBinderTransactionWrapper channelWorkerBinderTransactionWrapper;

    @Autowired
    PersistenceServiceImpl persistenceService;

    @Test
    void testChannelVideosPersistence() throws InterruptedException {
        String channelId1 = "chId01__________________";
        String channelId2 = "chId02__________________";
        channelService.newPendingChannel(channelId1);
        channelService.newPendingChannel(channelId2);
        ChannelEntity channelEntity1 = channelService.getById(channelId1);
        ChannelEntity channelEntity2 = channelService.getById(channelId2);
        assertEquals(StatusCode.PENDING, channelEntity1.getContextStatus().getStatusCode());
        assertEquals(StatusCode.PENDING, channelEntity2.getContextStatus().getStatusCode());

        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> channelWorkerBinderTransactionWrapper.bindPendingChannelToWorker(32, true, 5000));
        service.submit(() -> channelWorkerBinderTransactionWrapper.bindPendingChannelToWorker(56, true, 5000));
        service.shutdown();

        Thread.sleep(2000);

        channelEntity1 = channelService.getById(channelId1);
        channelEntity2 = channelService.getById(channelId2);

        Set<Integer> workerIdSet = new HashSet<>();

        workerIdSet.add(channelEntity1.getWorkerId());
        workerIdSet.add(channelEntity2.getWorkerId());

        assertTrue(workerIdSet.contains(32));
        assertTrue(workerIdSet.contains(56));

        service.awaitTermination(5, TimeUnit.SECONDS);

        channelEntity1 = channelService.getById(channelId1);
        channelEntity2 = channelService.getById(channelId2);

        assertNull(channelEntity1.getWorkerId());
        assertNull(channelEntity2.getWorkerId());
    }

}