package com.ctzn.youtubescraper.persistence;

import com.ctzn.youtubescraper.config.CommentIteratorCfg;
import com.ctzn.youtubescraper.config.CommentOrderCfg;
import com.ctzn.youtubescraper.handler.DataCollector;
import com.ctzn.youtubescraper.model.comments.CommentDTO;
import com.ctzn.ytsservice.domain.shared.CommentEntity;
import com.ctzn.ytsservice.domain.shared.VideoEntity;
import com.ctzn.ytsservice.domain.shared.WorkerLogEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

class PersistenceCommentRunner implements Runnable {

    private final String videoId;
    private final Map<String, VideoEntity> videoEntityMap;
    private final PersistenceService persistenceService;
    private final CommentOrderCfg commentOrderCfg;
    private final CommentIteratorCfg commentIteratorCfg;

    PersistenceCommentRunner(String videoId, Map<String, VideoEntity> videoEntityMap, PersistenceService persistenceService, CommentOrderCfg commentOrderCfg, CommentIteratorCfg commentIteratorCfg) {
        this.videoId = videoId;
        this.videoEntityMap = videoEntityMap;
        this.persistenceService = persistenceService;
        this.commentOrderCfg = commentOrderCfg;
        this.commentIteratorCfg = commentIteratorCfg;
    }

    @Override
    public void run() {
        WorkerLogEntity logEntry = new WorkerLogEntity(null, videoId, new Date(), null, "STARTED", toString());
        persistenceService.saveOrUpdateWorkerLog(logEntry);
        DataCollector<CommentDTO> collector = new DataCollector<>();
        CommentRunnerFactory.newInstance(videoId, collector, commentOrderCfg, commentIteratorCfg).run();

        List<CommentEntity> commentEntities = collector.stream().filter(c -> c.getParentCommentId() == null)
                .map(c -> CommentEntity.fromCommentDTO(c, videoEntityMap, null)).collect(Collectors.toList());
        Map<String, CommentEntity> commentEntityMap = commentEntities.stream().collect(Collectors.toMap(CommentEntity::getCommentId, e -> e));
        List<CommentEntity> replyEntities = collector.stream().filter(c -> c.getParentCommentId() != null)
                .map(c -> CommentEntity.fromCommentDTO(c, videoEntityMap, commentEntityMap)).collect(Collectors.toList());

        persistenceService.saveComments(commentEntities, replyEntities);

        logEntry.setFinishedDate(new Date());
        int cs = commentEntities.size(), rs = replyEntities.size();
        // TODO rewrite the comment runner to make it return exceptions to log error messages here
        logEntry.setStatus(String.format("DONE: total: %d, comments: %d, replies: %d", cs + rs, cs, rs));
        persistenceService.saveOrUpdateWorkerLog(logEntry);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ")
                .add("videoId='" + videoId + "'")
                .add(commentOrderCfg.toString())
                .add(commentIteratorCfg.toString())
                .toString();
    }

}
