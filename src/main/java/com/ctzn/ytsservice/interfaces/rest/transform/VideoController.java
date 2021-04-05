package com.ctzn.ytsservice.interfaces.rest.transform;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.VideoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private VideoRepository videoRepository;
    private ObjectAssembler domainMapper;
    private SortColumnNamesAdapter sortColumnNamesAdapter;

    public VideoController(VideoRepository videoRepository, ObjectAssembler domainMapper, SortColumnNamesAdapter sortColumnNamesAdapter) {
        this.videoRepository = videoRepository;
        this.domainMapper = domainMapper;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<VideoResponse>> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<VideoEntity> page = text == null || text.isEmpty() || text.isBlank() ?
                // if filtering query param is missing, disable sorting to improve performance
                videoRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                // native full text search
                videoRepository.nativeFts(text, sortColumnNamesAdapter.adapt(pageable, VideoEntity.class));
//                // true full text look up
//                commentRepository.findAllByTextContainingIgnoreCase(text, pageable)
        return ResponseEntity.ok().body(domainMapper.fromVideoPageToPagedResponse(page));
    }

}
