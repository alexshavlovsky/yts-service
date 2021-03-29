package com.ctzn.ytsservice.domain.channelrunner;

import com.ctzn.youtubescraper.persistence.PersistenceService;
import com.ctzn.ytsservice.domain.shared.ChannelEntity;
import com.ctzn.ytsservice.domain.shared.CommentEntity;
import com.ctzn.ytsservice.domain.shared.VideoEntity;
import com.ctzn.ytsservice.domain.shared.WorkerLogEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.WorkerLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersistenceServiceImpl implements PersistenceService {

    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final WorkerLogRepository workerLogRepository;

    public PersistenceServiceImpl(ChannelRepository channelRepository, VideoRepository videoRepository, CommentRepository commentRepository, WorkerLogRepository workerLogRepository) {
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
        this.workerLogRepository = workerLogRepository;
    }

    public void saveChannel(ChannelEntity channelEntity, List<VideoEntity> videoEntities) {
        channelRepository.save(channelEntity);
        videoRepository.saveAll(videoEntities);
    }

    public void saveComments(List<CommentEntity> commentEntities, List<CommentEntity> replyEntities) {
        commentRepository.saveAll(commentEntities);
        commentRepository.saveAll(replyEntities);
    }

    public void saveOrUpdateWorkerLog(WorkerLogEntity logEntry) {
        if (logEntry.getId() == null) {
            WorkerLogEntity saved = workerLogRepository.save(logEntry);
            logEntry.setId(saved.getId());
        } else workerLogRepository.save(logEntry);
    }

}
