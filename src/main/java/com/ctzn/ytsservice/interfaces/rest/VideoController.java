package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.application.service.VideoService;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.query.VideoQueryRequest;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.VideoIdRequest;
import com.ctzn.ytsservice.interfaces.rest.transform.GenericCriteriaBuilder;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ctzn.ytsservice.interfaces.rest.exception.ResourceException.*;

@Slf4j
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
        if (videoSummary == null) throw videoNotFound(videoId);
        return ResponseEntity.ok().body(videoSummary);
    }

    @PutMapping("")
    public ResponseEntity<VideoIdRequest> updateVideo(@RequestBody @Valid VideoIdRequest dto) {
        String videoId = dto.getVideoId();
        VideoEntity videoEntity = videoService.getById(videoId);
        if (videoEntity == null) throw videoNotFound(videoId);
        StatusCode statusCode = videoEntity.getContextStatus().getStatusCode();
        if (statusCode == StatusCode.PENDING) throw videoScheduled(videoId);
        if (statusCode == StatusCode.LOCKED_FOR_DELETE) throw videoScheduled(videoId);
        Integer workerId = videoEntity.getWorkerId();
        if (workerId != null) throw videoPassedToWorker(videoId, workerId);
        videoEntity.getContextStatus().setStatusCode(StatusCode.PENDING);
        videoService.save(videoEntity);
        log.info("Video scheduled for update: {videoId={}}", videoId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{videoId}")
    public ResponseEntity<VideoIdRequest> deleteVideo(@Valid VideoIdRequest dto) {
        String videoId = dto.getVideoId();
        if (!videoService.isExistById(videoId)) throw videoNotFound(videoId);
        log.info("Deleting a video: {videoId={}}", videoId);
        videoService.deleteById(videoId);
        log.info("Deleted a video: {videoId={}}", videoId);
        return ResponseEntity.ok().body(dto);
    }

}
