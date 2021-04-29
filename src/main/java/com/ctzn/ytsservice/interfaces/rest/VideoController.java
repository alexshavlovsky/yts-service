package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.application.service.VideoService;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.query.VideoQueryRequest;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.VideoIdRequest;
import com.ctzn.ytsservice.interfaces.rest.transform.GenericCriteriaBuilder;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Log
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final ObjectAssembler domainMapper;
    private final GenericCriteriaBuilder queryBuilder;

    public VideoController(VideoService videoService, ObjectAssembler domainMapper, GenericCriteriaBuilder queryBuilder) {
        this.videoService = videoService;
        this.domainMapper = domainMapper;
        this.queryBuilder = queryBuilder;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<VideoResponse>> findByQuery(VideoQueryRequest dto, Pageable pageable) {
        Page<VideoEntity> page = queryBuilder.getPage(dto, pageable, VideoEntity.class);
        return ResponseEntity.ok().body(domainMapper.fromVideoPageToPagedResponse(page));
    }

    @GetMapping("{videoId}")
    public ResponseEntity<VideoSummaryResponse> getVideoSummary(@Valid VideoIdRequest dto) {
        String videoId = dto.getVideoId();
        VideoSummaryResponse videoSummary = videoService.getVideoSummary(videoId);
        if (videoId == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(videoSummary);
    }

    @DeleteMapping("{videoId}")
    public ResponseEntity<VideoIdRequest> deleteVideo(@Valid VideoIdRequest dto) {
        String videoId = dto.getVideoId();
        if (!videoService.isExistById(videoId)) {
            log.warning("Video not fount: " + videoId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info("Deleting a video: " + videoId);
        videoService.deleteById(videoId);
        log.info("Deleted a video: " + videoId);
        return ResponseEntity.ok().body(dto);
    }

}
