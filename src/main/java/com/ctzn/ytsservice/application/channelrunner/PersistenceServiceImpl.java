package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.PersistenceService;
import com.ctzn.youtubescraper.core.persistence.dto.*;
import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.application.service.CommentService;
import com.ctzn.ytsservice.application.service.VideoService;
import com.ctzn.ytsservice.domain.entities.*;
import com.ctzn.ytsservice.infrastrucure.repositories.WorkerLogRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log
public class PersistenceServiceImpl implements PersistenceService {

    private final ChannelService channelService;
    private final VideoService videoService;
    private final CommentService commentService;
    private final WorkerLogRepository workerLogRepository;

    public PersistenceServiceImpl(ChannelService channelService, VideoService videoService, CommentService commentService, WorkerLogRepository workerLogRepository) {
        this.channelService = channelService;
        this.videoService = videoService;
        this.commentService = commentService;
        this.workerLogRepository = workerLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveChannelVideos(ChannelVideosDTO channelVideos) {
        ContextStatusDTO contextStatusDTO = new ContextStatusDTO(StatusCode.PASSED_TO_WORKER,
                String.format("Video metadata collected: %d of %d", channelVideos.getVideos().size(), channelVideos.getChannel().getVideoCount())
        );
        saveWorkerLog(new WorkerLogDTO(channelVideos.getChannel().getChannelId(), contextStatusDTO));

        ChannelEntity channelEntity = channelService.createOrUpdateAndGet(channelVideos.getChannel(), contextStatusDTO);
        channelService.save(channelEntity);

        List<VideoEntity> videos = channelVideos.getVideos().stream()
                .map(videoDTO -> videoService.createOrUpdateAndGet(videoDTO, channelEntity))
                .collect(Collectors.toList());
        videoService.saveAll(videos);
    }

    @Override
    public void saveVideoComments(String videoId, List<CommentDTO> comments, List<CommentDTO> replies) {
        VideoEntity videoEntity = videoService.getById(videoId);
        if (videoEntity == null) {
            log.warning("Can't save comments. The parent video doesn't exist: " + videoId);
            return;
        }
        List<CommentEntity> entities =
                Stream.concat(comments.stream(), replies.stream())
                        .map(commentDTO -> commentService.createOrUpdateAndGet(commentDTO, videoEntity))
                        .collect(Collectors.toList());
        commentService.saveAll(entities);
    }

    @Override
    public void updateVideoTotalCommentCount(String videoId, int totalCommentCount) {
        VideoEntity videoEntity = videoService.getById(videoId);
        if (videoEntity == null) {
            log.warning("Can't update total comment count The video doesn't exist: " + videoId);
            return;
        }
        videoEntity.setTotalCommentCount(totalCommentCount);
        videoService.save(videoEntity);
    }

    @Override
    public void saveWorkerLog(WorkerLogDTO logEntry) {
        workerLogRepository.save(WorkerLogEntity.fromWorkerLogDTO(logEntry));
    }

    @Override
    public void setChannelStatus(String channelId, ContextStatusDTO status) {
        ChannelEntity channelEntity = channelService.getById(channelId);
        if (channelEntity == null) {
            log.warning("Can't set channel status. The channel doesn't exist: " + channelId);
            return;
        }
        channelEntity.setContextStatus(ContextStatus.fromContextStatusDTO(status));
        channelService.save(channelEntity);
    }

    @Override
    public void setVideoStatus(String videoId, ContextStatusDTO status) {
        VideoEntity videoEntity = videoService.getById(videoId);
        if (videoEntity == null) {
            log.warning("Can't save comments. The video doesn't exist: " + videoId);
            return;
        }
        videoEntity.setContextStatus(ContextStatus.fromContextStatusDTO(status));
        videoService.save(videoEntity);
    }

    @Override
    public void logVideo(String videoId, StatusCode statusCode, String statusMessage) {
        PersistenceService.super.logVideo(videoId, statusCode, statusMessage);
    }

    @Override
    public void logChannel(String channelId, StatusCode statusCode, String statusMessage) {
        PersistenceService.super.logChannel(channelId, statusCode, statusMessage);
    }

}
