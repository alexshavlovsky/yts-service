package com.ctzn.youtubescraper.persistence;

import com.ctzn.youtubescraper.config.CommentIteratorCfg;
import com.ctzn.youtubescraper.config.CommentOrderCfg;
import com.ctzn.youtubescraper.config.ExecutorCfg;
import com.ctzn.youtubescraper.config.VideoIteratorCfg;
import com.ctzn.youtubescraper.exception.ScraperException;
import com.ctzn.youtubescraper.executor.CustomExecutorService;
import com.ctzn.youtubescraper.model.channelvideos.ChannelDTO;
import com.ctzn.ytsservice.domain.shared.ChannelEntity;
import com.ctzn.ytsservice.domain.shared.VideoEntity;
import com.ctzn.ytsservice.domain.shared.WorkerLogEntity;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class PersistenceChannelRunner implements Callable<Void> {

    private final String channelId;
    private final PersistenceService persistenceService;
    private final ExecutorCfg executorCfg;
    private final CommentOrderCfg commentOrderCfg;
    private final VideoIteratorCfg videoIteratorCfg;
    private final CommentIteratorCfg commentIteratorCfg;

    public PersistenceChannelRunner(String channelId, PersistenceService persistenceService, ExecutorCfg executorCfg, CommentOrderCfg commentOrderCfg, VideoIteratorCfg videoIteratorCfg, CommentIteratorCfg commentIteratorCfg) {
        this.channelId = channelId;
        this.persistenceService = persistenceService;
        this.executorCfg = executorCfg;
        this.commentOrderCfg = commentOrderCfg;
        this.videoIteratorCfg = videoIteratorCfg;
        this.commentIteratorCfg = commentIteratorCfg;
    }

    public static PersistenceChannelRunnerStepBuilder.ExecutorStep newBuilder(String channelId, PersistenceService persistenceService) {
        return PersistenceChannelRunnerStepBuilder.newBuilder(channelId, persistenceService);
    }

    private Map<String, VideoEntity> grabChannelData(String channelId) throws ScraperException {
        ChannelVideosCollector collector = new ChannelVideosCollector(channelId);
        ChannelDTO channel = collector.call();
        ChannelEntity channelEntity = ChannelEntity.fromChannelDTO(channel);
        List<VideoEntity> videoEntities =
                (videoIteratorCfg.getVideoCountLimit().isUnrestricted() ?
                        channel.getVideos().stream() :
                        channel.getVideos().stream().limit(videoIteratorCfg.getVideoCountLimit().get())
                ).map(v -> VideoEntity.fromVideoDTO(v, channelEntity)).collect(Collectors.toList());
        persistenceService.saveChannel(channelEntity, videoEntities);
        return videoEntities.stream().collect(LinkedHashMap::new, (map, video) -> map.put(video.getVideoId(), video), Map::putAll);
    }

    private void grabComments(Map<String, VideoEntity> videoEntityMap) throws InterruptedException {
        executorCfg.addThreadNameSegment(channelId);
        CustomExecutorService executor = executorCfg.build();
        videoEntityMap.keySet().stream()
                .map(videoId -> new PersistenceCommentRunner(videoId, videoEntityMap, persistenceService, commentOrderCfg, commentIteratorCfg))
                .forEach(executor::submit);
        executor.awaitAndTerminate();
    }

    @Override
    public Void call() throws Exception {
        WorkerLogEntity logEntry = new WorkerLogEntity(null, channelId, new Date(), null, "STARTED", toString());
        persistenceService.saveOrUpdateWorkerLog(logEntry);
        try {
            Map<String, VideoEntity> videoEntityMap = grabChannelData(channelId);
            grabComments(videoEntityMap);
            logEntry.setFinishedDate(new Date());
            logEntry.setStatus("DONE: videos: " + videoEntityMap.size());
            persistenceService.saveOrUpdateWorkerLog(logEntry);
        } catch (Exception e) {
            logEntry.setFinishedDate(new Date());
            logEntry.setStatus("EXCEPTION: " + e.getMessage());
            persistenceService.saveOrUpdateWorkerLog(logEntry);
            throw e;
        }
        return null;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ")
                .add("channelId='" + channelId + "'")
                .add(executorCfg.toString())
                .add(commentOrderCfg.toString())
                .add(videoIteratorCfg.toString())
                .add(commentIteratorCfg.toString())
                .toString();
    }

}
