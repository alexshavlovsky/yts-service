package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.application.service.VideoService;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private VideoService videoService;
    private ObjectAssembler domainMapper;

    public VideoController(VideoService videoService, ObjectAssembler domainMapper) {
        this.videoService = videoService;
        this.domainMapper = domainMapper;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<VideoResponse>> findByTextContaining(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "channelId", required = false) String channelId,
            Pageable pageable) {
        Page<VideoEntity> page = channelId != null ?
                videoService.getVideosByChannel(channelId, text, pageable) :
                videoService.getVideos(text, pageable, false);
        return ResponseEntity.ok().body(domainMapper.fromVideoPageToPagedResponse(page));
    }

    @GetMapping("{videoId}")
    public ResponseEntity<VideoSummaryResponse> getChannelSummary(@PathVariable("videoId") String videoId) {
        VideoSummaryResponse videoSummary = videoService.getVideoSummary(videoId);
        if (videoId == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(videoSummary);
    }

}
