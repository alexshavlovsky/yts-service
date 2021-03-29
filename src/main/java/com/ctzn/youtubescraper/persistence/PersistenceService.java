package com.ctzn.youtubescraper.persistence;

import com.ctzn.ytsservice.domain.shared.ChannelEntity;
import com.ctzn.ytsservice.domain.shared.CommentEntity;
import com.ctzn.ytsservice.domain.shared.VideoEntity;
import com.ctzn.ytsservice.domain.shared.WorkerLogEntity;

import java.util.List;

public interface PersistenceService {

    void saveChannel(ChannelEntity channelEntity, List<VideoEntity> videoEntities);

    void saveComments(List<CommentEntity> commentEntities, List<CommentEntity> replyEntities);

    void saveOrUpdateWorkerLog(WorkerLogEntity logEntry);

}
