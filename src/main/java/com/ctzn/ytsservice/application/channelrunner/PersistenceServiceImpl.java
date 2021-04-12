package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.PersistenceService;
import com.ctzn.youtubescraper.core.persistence.dto.*;
import com.ctzn.ytsservice.domain.entities.*;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.WorkerLogRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ctzn.ytsservice.domain.entities.VideoEntity.fromVideoDTO;

@Service
@Log
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

    @Override
    public void saveChannelVideos(ChannelVideosDTO channelVideos) {
        ContextStatusDTO contextStatusDTO = new ContextStatusDTO(StatusCode.METADATA_FETCHED,
                String.format("Videos collected: %d of %d", channelVideos.getVideos().size(), channelVideos.getChannel().getVideoCount())
        );
        saveWorkerLog(new WorkerLogDTO(channelVideos.getChannel().getChannelId(), contextStatusDTO));
        ChannelEntity channelEntity = channelRepository.save(ChannelEntity.fromChannelDTO(
                channelVideos.getChannel(),
                ContextStatus.fromContextStatusDTO(contextStatusDTO)
        ));
        List<VideoEntity> videos = channelVideos.getVideos().stream()
                .map(videoDTO -> fromVideoDTO(videoDTO, channelEntity)).collect(Collectors.toList());
        videoRepository.saveAll(videos);
    }

    @Override
    public void saveVideoComments(String videoId, List<CommentDTO> comments, List<CommentDTO> replies) {
        VideoEntity videoEntity = videoRepository.findById(videoId).orElse(null);
        if (videoEntity == null) {
            log.warning("Can't save comments. The parent video doesn't exist: " + videoId);
            return;
        }

        Map<String, CommentEntity> commentEntityMap = CommentEntity.getCommentMap(videoEntity, comments);
        commentRepository.saveAll(commentEntityMap.values());

        List<CommentEntity> replyEntities = CommentEntity.getReplyList(videoEntity, replies, commentEntityMap);
        commentRepository.saveAll(replyEntities);
    }

    @Override
    public void updateVideoTotalCommentCount(String videoId, int totalCommentCount) {
        VideoEntity videoEntity = videoRepository.findById(videoId).orElse(null);
        if (videoEntity == null) {
            log.warning("Can't update total comment count The video doesn't exist: " + videoId);
            return;
        }
        videoEntity.setTotalCommentCount(totalCommentCount);
        videoRepository.save(videoEntity);
    }

    @Override
    public void saveWorkerLog(WorkerLogDTO logEntry) {
        workerLogRepository.save(WorkerLogEntity.fromWorkerLogDTO(logEntry));
    }

    @Override
    public void setChannelStatus(String channelId, ContextStatusDTO status) {
        Optional<ChannelEntity> optional = channelRepository.findById(channelId);
        if (optional.isEmpty()) {
            log.warning("Can't set channel status. The channel doesn't exist: " + channelId);
            return;
        }
        ChannelEntity channelEntity = optional.get();
        channelEntity.setContextStatus(ContextStatus.fromContextStatusDTO(status));
        channelRepository.save(channelEntity);
    }

    @Override
    public void setVideoStatus(String videoId, ContextStatusDTO status) {
        VideoEntity videoEntity = videoRepository.findById(videoId).orElse(null);
        if (videoEntity == null) {
            log.warning("Can't save comments. The parent video doesn't exist: " + videoId);
            return;
        }
        videoEntity.setContextStatus(ContextStatus.fromContextStatusDTO(status));
        videoRepository.save(videoEntity);
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
