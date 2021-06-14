package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.application.service.VideoService;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ReadableResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.video.VideoResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.video.VideoSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.query.VideoQueryRequest;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.VideoIdRequest;
import com.ctzn.ytsservice.interfaces.rest.transform.GenericCriteriaBuilder;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.ResponseFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ctzn.ytsservice.interfaces.rest.exception.ResourceException.*;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final ObjectAssembler domainMapper;
    private final GenericCriteriaBuilder queryBuilder;
    private final ResponseFormatter responseFormatter;

    public VideoController(VideoService videoService, ObjectAssembler domainMapper, GenericCriteriaBuilder queryBuilder, ResponseFormatter responseFormatter) {
        this.videoService = videoService;
        this.domainMapper = domainMapper;
        this.queryBuilder = queryBuilder;
        this.responseFormatter = responseFormatter;
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
    public ResponseEntity<ReadableResponse> updateVideo(@RequestBody @Valid VideoIdRequest dto) {
        String videoId = dto.getVideoId();
        VideoEntity videoEntity = videoService.getById(videoId);
        if (videoEntity == null) throw videoNotFound(videoId);
        StatusCode statusCode = videoEntity.getContextStatus().getStatusCode();
        if (statusCode == StatusCode.PENDING) throw videoScheduled(videoId);
        if (statusCode == StatusCode.LOCKED_FOR_DELETE) throw videoLockedForDelete(videoId);
        Integer workerId = videoEntity.getWorkerId();
        if (workerId != null) throw videoPassedToWorker(videoId, workerId);
        videoEntity.getContextStatus().setStatusCode(StatusCode.PENDING);
        videoService.save(videoEntity);
        return responseFormatter.getResponse(videoId, "Video scheduled for update: [videoId: {}]", videoId);
    }

    @DeleteMapping("{videoId}")
    public ResponseEntity<ReadableResponse> deleteVideo(@Valid VideoIdRequest dto) {
        String videoId = dto.getVideoId();
        if (!videoService.isExistById(videoId)) throw videoNotFound(videoId);
        // TODO: set video status LOCKED_FOR_DELETE before deletion
        videoService.deleteById(videoId);
        return responseFormatter.getResponse(videoId, "Video deleted: [videoId: {}]", videoId);
    }

}
