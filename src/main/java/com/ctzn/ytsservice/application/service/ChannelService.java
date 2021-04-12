package com.ctzn.ytsservice.application.service;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.domain.entities.WorkerLogEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.WorkerLogRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.*;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChannelService {

    private ChannelRepository repository;
    private WorkerLogRepository workerLogRepository;
    private SortColumnNamesAdapter sortColumnNamesAdapter;
    private ObjectAssembler objectAssembler;

    public ChannelService(ChannelRepository repository, WorkerLogRepository workerLogRepository, SortColumnNamesAdapter sortColumnNamesAdapter, ObjectAssembler objectAssembler) {
        this.repository = repository;
        this.workerLogRepository = workerLogRepository;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
        this.objectAssembler = objectAssembler;
    }

    public Page<ChannelEntity> getChannels(String rawQuery, Pageable pageable, boolean optimize) {
        boolean noFiltering = rawQuery == null || rawQuery.isEmpty() || rawQuery.isBlank();
        if (optimize) {
            return noFiltering ?
                    repository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    repository.nativeFts(rawQuery, sortColumnNamesAdapter.adapt(pageable, ChannelEntity.class));
        } else {
            return noFiltering ?
                    repository.findAll(pageable) :
                    repository.findAllByTitleContainingIgnoreCase(rawQuery, pageable);
        }
    }

    public void deleteChannel(String channelId) {
        repository.deleteById(channelId);
    }

    public ChannelSummaryResponse getChannelSummary(String channelId) {
        ChannelEntity channelEntity = repository.findById(channelId).orElse(null);
        if (channelEntity == null) return null;
        List<WorkerLogEntity> workerLogEntities = workerLogRepository.getAllByContextId(channelId);
        ChannelDetailedResponse channel = objectAssembler.map(channelEntity, ChannelDetailedResponse.class);
        List<VideoEntity> videoEntities = channelEntity.getVideos();
        List<VideoDetailedResponse> videos = videoEntities.stream().map(ve -> objectAssembler.map(ve, VideoDetailedResponse.class)).collect(Collectors.toList());
        List<WorkerLogResponse> log = workerLogEntities.stream().map(wl -> objectAssembler.map(wl, WorkerLogResponse.class)).collect(Collectors.toList());
        int totalComments = videos.stream().filter(v -> v.getTotalCommentCount() != null).mapToInt(VideoResponse::getTotalCommentCount).sum();
        return new ChannelSummaryResponse(channel, videos, log, totalComments);
    }

}
