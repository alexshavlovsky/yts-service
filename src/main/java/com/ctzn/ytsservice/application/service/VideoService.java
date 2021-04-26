package com.ctzn.ytsservice.application.service;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.youtubescraper.core.persistence.dto.VideoDTO;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.ContextStatus;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.domain.entities.VideoNaturalId;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoDetailedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoService {

    private VideoRepository repository;
    private CommentRepository commentRepository;
    private WorkerLogService workerLogService;
    private SortColumnNamesAdapter sortColumnNamesAdapter;
    private ObjectAssembler objectAssembler;

    public VideoService(VideoRepository repository, CommentRepository commentRepository, WorkerLogService workerLogService, SortColumnNamesAdapter sortColumnNamesAdapter, ObjectAssembler objectAssembler) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.workerLogService = workerLogService;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
        this.objectAssembler = objectAssembler;
    }

    public Page<VideoEntity> getVideos(String rawQuery, Pageable pageable, boolean optimize) {
        boolean noFiltering = rawQuery == null || rawQuery.isEmpty() || rawQuery.isBlank();
        if (optimize) {
            return noFiltering ?
                    repository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    repository.nativeFts(rawQuery, sortColumnNamesAdapter.adapt(pageable, VideoEntity.class));
        } else {
            return noFiltering ?
                    repository.findAll(pageable) :
                    repository.findAllByTitleContainingIgnoreCase(rawQuery, pageable);
        }
    }

    public VideoEntity createOrUpdateAndGet(VideoDTO videoDTO, ChannelEntity channel) {
        String videoId = videoDTO.getVideoId();
        ContextStatus contextStatus = new ContextStatus(StatusCode.METADATA_FETCHED);
        VideoEntity persistentVideo = repository.findByNaturalId_videoId(videoId).orElse(null);
        if (persistentVideo == null) {
            return VideoEntity.fromVideoDTO(
                    new VideoNaturalId(null, videoId),
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
        VideoEntity videoEntity = repository.findByNaturalId_videoId(videoId).orElse(null);
        if (videoEntity == null) return null;
        VideoDetailedResponse video = objectAssembler.map(videoEntity, VideoDetailedResponse.class);
        Long totalComments = commentRepository.countComments(videoId);
        if (totalComments == null) totalComments = 0L;
        return new VideoSummaryResponse(video, workerLogService.getByContextId(videoId), totalComments.intValue());
    }

    public void deleteVideo(String videoId) {
        repository.deleteByNaturalId_videoId(videoId);
    }

    public void saveAll(List<VideoEntity> videos) {
        repository.saveAll(videos);
    }

    public VideoEntity getById(String videoId) {
        return repository.findByNaturalId_videoId(videoId).orElse(null);
    }

    public void save(VideoEntity videoEntity) {
        repository.save(videoEntity);
    }

}
