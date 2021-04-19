package com.ctzn.ytsservice.application.service;

import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.domain.entities.WorkerLogEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.WorkerLogRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoDetailedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.WorkerLogResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VideoService {

    private VideoRepository repository;
    private CommentRepository commentRepository;
    private WorkerLogRepository workerLogRepository;
    private SortColumnNamesAdapter sortColumnNamesAdapter;
    private ObjectAssembler objectAssembler;

    public VideoService(VideoRepository repository, CommentRepository commentRepository, WorkerLogRepository workerLogRepository, SortColumnNamesAdapter sortColumnNamesAdapter, ObjectAssembler objectAssembler) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.workerLogRepository = workerLogRepository;
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

    public Page<VideoEntity> getVideosByChannel(String channelId, String text, Pageable pageable) {
        return text == null ?
                repository.findAllByChannel_channelId(channelId, pageable) :
                repository.findAllByChannel_channelIdAndTitleContainingIgnoreCase(channelId, text, pageable);
    }

    public VideoSummaryResponse getVideoSummary(String videoId) {
        VideoEntity videoEntity = repository.findById(videoId).orElse(null);
        if (videoEntity == null) return null;
        VideoDetailedResponse video = objectAssembler.map(videoEntity, VideoDetailedResponse.class);
        List<WorkerLogEntity> workerLogEntities = workerLogRepository.getAllByContextId(videoId);
        List<WorkerLogResponse> log = workerLogEntities.stream().map(wl -> objectAssembler.map(wl, WorkerLogResponse.class)).collect(Collectors.toList());
        int totalComments = (int) commentRepository.countByVideo_videoId(videoId);
        return new VideoSummaryResponse(video, log, totalComments);
    }

}
