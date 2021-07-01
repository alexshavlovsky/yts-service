package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.PersistenceRunner;
import com.ctzn.youtubescraper.core.persistence.PersistenceRunnerStepBuilder;
import com.ctzn.youtubescraper.core.persistence.PersistenceService;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.ChannelRunnerConfigDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class RunnerFactory {

    @Value("${app.scraper.runner.thread.number:10}")
    Integer numberOfThreads;

    private final PersistenceService persistenceService;

    public RunnerFactory(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public PersistenceRunner newChannelRunner(String channelId) {
        return PersistenceRunner.newChannelRunnerBuilder(channelId, persistenceService)
                .withExecutor(numberOfThreads, Duration.ofHours(1)).processAllComments().build();
    }

    public PersistenceRunner newChannelRunner(ChannelRunnerConfigDTO config) {
        return newChannelRunnerBuilder(config).build();
    }

    public PersistenceRunnerStepBuilder.BuildStep newChannelRunnerBuilder(ChannelRunnerConfigDTO config) {
        PersistenceRunnerStepBuilder.CommentOrderStep cos = PersistenceRunner
                .newChannelRunnerBuilder(config.getChannelIdInput(), persistenceService)
                .withExecutor(config.getNumberOfThreads(), config.getExecutorTimeout());
        PersistenceRunnerStepBuilder.VideoIteratorStep vis = config.isOrderNewestFirst() ?
                cos.newestCommentsFirst() :
                cos.topCommentsFirst();
        PersistenceRunnerStepBuilder.CommentIteratorStep cis = config.isVideoNoLimit() ?
                vis.processAllChannelVideos() :
                vis.videoCountLimit(config.getVideoLimitValue());
        int cl = config.isCommentNoLimit() ? Integer.MAX_VALUE : config.getCommentLimitValue();
        int rl = config.isReplyNoLimit() ? Integer.MAX_VALUE : config.getReplyLimitValue();
        return config.isCommentNoLimit() && config.isReplyNoLimit() ? cis.processWithNoLimits() : cis.commentCountLimits(cl, rl);
    }

    public PersistenceRunner newVideoListRunner(List<String> videoIds) {
        return PersistenceRunner.newVideoListRunnerBuilder(videoIds, persistenceService)
                .withExecutor(numberOfThreads, Duration.ofHours(1)).processAllComments().build();
    }

}
