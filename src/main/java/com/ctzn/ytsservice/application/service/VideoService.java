package com.ctzn.ytsservice.application.service;

import com.ctzn.ytsservice.domain.entities.VideoEntity;
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

    public VideoSummaryResponse getVideoSummary(String videoId) {
        VideoEntity videoEntity = repository.findById(videoId).orElse(null);
        if (videoEntity == null) return null;
        VideoDetailedResponse video = objectAssembler.map(videoEntity, VideoDetailedResponse.class);
        Long totalComments = commentRepository.countComments(videoId);
        if (totalComments == null) totalComments = 0L;
        return new VideoSummaryResponse(video, workerLogService.getByContextId(videoId), totalComments.intValue());
    }

    public void deleteVideo(String videoId) {
        repository.deleteById(videoId);
    }

}
