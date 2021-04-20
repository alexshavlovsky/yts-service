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

    private VideoService videoService;
    private ObjectAssembler domainMapper;
    private GenericCriteriaBuilder queryBuilder;

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
    public ResponseEntity<VideoSummaryResponse> getChannelSummary(@Valid VideoIdRequest dto) {
        String videoId = dto.getVideoId();
        VideoSummaryResponse videoSummary = videoService.getVideoSummary(videoId);
        if (videoId == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(videoSummary);
    }

    @DeleteMapping("{videoId}")
    public ResponseEntity<VideoIdRequest> deleteVideo(@Valid VideoIdRequest dto) {
        String videoId = dto.getVideoId();
        log.info("Delete a video: " + videoId);
        videoService.deleteVideo(videoId);
        log.info("OK deleting a video: " + videoId);
        return ResponseEntity.ok().body(dto);
    }

}

// sub query: users per video commented at least once
//====================================================
// SELECT
// video_id,
// channel_id as user_id,
// author_text as user_title,
// count(1) as comment_count,
// sum(like_count) as like_count,
// sum(reply_count) as reply_count
// FROM comments
// GROUP BY (video_id, channel_id)

// users by commented video count
//================================
// SELECT
// user_id,
// user_title,
// count(1) as commented_video_count,
// sum(comment_count) as comment_count,
// sum(like_count) as like_count,
// sum(reply_count) as reply_count
// FROM (
// SELECT
// video_id,
// channel_id as user_id,
// author_text as user_title,
// count(1) as comment_count,
// sum(like_count) as like_count,
// sum(reply_count) as reply_count
// FROM comments
// GROUP BY (video_id, channel_id)
// )
// GROUP BY user_id
// ORDER BY commented_video_count DESC

// commented videos by user
//==========================
// SELECT DISTINCT
// video_id
// FROM
// comments
// WHERE channel_id = 'UCB12jjYsYv-eipCvBDcMbXw'

// users commented on ten same videos
//===================================
// SELECT
// user_id,
// user_title,
// count(1) as commented_video_count,
// sum(comment_count) as comment_count,
// sum(like_count) as like_count,
// sum(reply_count) as reply_count
// FROM (
// SELECT
// video_id,
// channel_id as user_id,
// author_text as user_title,
// count(1) as comment_count,
// sum(like_count) as like_count,
// sum(reply_count) as reply_count
// FROM comments
// GROUP BY (video_id, channel_id)
// )
// WHERE video_id in (
// SELECT DISTINCT
// video_id
// FROM
// comments
// WHERE channel_id = 'UCB12jjYsYv-eipCvBDcMbXw'
// )
// GROUP BY user_id
// HAVING commented_video_count > 1
// ORDER BY commented_video_count DESC
