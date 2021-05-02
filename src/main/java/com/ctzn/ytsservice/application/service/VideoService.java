package com.ctzn.ytsservice.application.service;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.youtubescraper.core.persistence.dto.VideoDTO;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.ContextStatus;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.domain.entities.VideoNaturalId;
import com.ctzn.ytsservice.infrastrucure.repositories.comment.AuthorChannelRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.comment.AuthorTextRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.comment.CommentRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.naturalid.CommentNaturalIdRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.naturalid.VideoNaturalIdRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoDetailedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class VideoService {

    private VideoRepository videoRepository;
    private VideoNaturalIdRepository videoNaturalIdRepository;
    private CommentNaturalIdRepository commentNaturalIdRepository;
    private CommentRepository commentRepository;
    private WorkerLogService workerLogService;
    private SortColumnNamesAdapter sortColumnNamesAdapter;
    private ObjectAssembler objectAssembler;
    private AuthorTextRepository authorTextRepository;
    private AuthorChannelRepository authorChannelRepository;

    public VideoService(VideoRepository videoRepository, VideoNaturalIdRepository videoNaturalIdRepository, CommentNaturalIdRepository commentNaturalIdRepository, CommentRepository commentRepository, WorkerLogService workerLogService, SortColumnNamesAdapter sortColumnNamesAdapter, ObjectAssembler objectAssembler, AuthorTextRepository authorTextRepository, AuthorChannelRepository authorChannelRepository) {
        this.videoRepository = videoRepository;
        this.videoNaturalIdRepository = videoNaturalIdRepository;
        this.commentNaturalIdRepository = commentNaturalIdRepository;
        this.commentRepository = commentRepository;
        this.workerLogService = workerLogService;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
        this.objectAssembler = objectAssembler;
        this.authorTextRepository = authorTextRepository;
        this.authorChannelRepository = authorChannelRepository;
    }

    public Page<VideoEntity> getVideos(String rawQuery, Pageable pageable, boolean optimize) {
        boolean noFiltering = rawQuery == null || rawQuery.isEmpty() || rawQuery.isBlank();
        if (optimize) {
            return noFiltering ?
                    videoRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    videoRepository.nativeFts(rawQuery, sortColumnNamesAdapter.adapt(pageable, VideoEntity.class));
        } else {
            return noFiltering ?
                    videoRepository.findAll(pageable) :
                    videoRepository.findAllByTitleContainingIgnoreCase(rawQuery, pageable);
        }
    }

    public VideoEntity createOrUpdateAndGet(VideoDTO videoDTO, ChannelEntity channel) {
        String videoId = videoDTO.getVideoId();
        ContextStatus contextStatus = new ContextStatus(StatusCode.METADATA_FETCHED);
        VideoEntity persistentVideo = videoRepository.findByNaturalId_videoId(videoId).orElse(null);
        if (persistentVideo == null) {
            return VideoEntity.fromVideoDTO(
                    VideoNaturalId.newFromPublicId(videoId),
                    videoDTO,
                    channel,
                    contextStatus
            );
        } else {
            objectAssembler.map(videoDTO, persistentVideo);
            objectAssembler.map(contextStatus, persistentVideo.getContextStatus());
            return persistentVideo;
        }
    }

    public VideoSummaryResponse getVideoSummary(String videoId) {
        VideoEntity videoEntity = videoRepository.findByNaturalId_videoId(videoId).orElse(null);
        if (videoEntity == null) return null;
        VideoDetailedResponse video = objectAssembler.map(videoEntity, VideoDetailedResponse.class);
        long totalComments = commentRepository.countByVideo_naturalId_videoId(videoId);
        return new VideoSummaryResponse(video, workerLogService.getByContextId(videoId), (int) totalComments);
    }

    public void save(VideoEntity videoEntity) {
        videoRepository.save(videoEntity);
    }

    public void saveAll(List<VideoEntity> videos) {
        videoRepository.saveAll(videos);
    }

    public VideoEntity getById(String videoId) {
        return videoRepository.findByNaturalId_videoId(videoId).orElse(null);
    }

    public boolean isExistById(String channelId) {
        return getById(channelId) != null;
    }

    @Transactional
    public void deleteById(String videoId) {
        videoRepository.deleteByNaturalId_videoId(videoId);
        videoNaturalIdRepository.deleteOrphans();
        commentNaturalIdRepository.deleteOrphans();
        authorTextRepository.deleteOrphans();
        authorChannelRepository.deleteOrphans();
    }

}
