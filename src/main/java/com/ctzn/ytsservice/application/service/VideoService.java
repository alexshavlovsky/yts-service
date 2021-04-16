package com.ctzn.ytsservice.application.service;

import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class VideoService {

    private VideoRepository repository;
    private SortColumnNamesAdapter sortColumnNamesAdapter;

    public VideoService(VideoRepository repository, SortColumnNamesAdapter sortColumnNamesAdapter) {
        this.repository = repository;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
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

}
