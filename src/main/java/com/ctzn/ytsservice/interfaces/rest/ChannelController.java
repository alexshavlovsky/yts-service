package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.ChannelIdRequest;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ctzn.ytsservice.interfaces.rest.exception.ResourceException.*;

@RestController
@RequestMapping("/api/channels")
@Slf4j
public class ChannelController {

    private final ChannelService channelService;
    private final ObjectAssembler domainMapper;

    public ChannelController(ChannelService channelService, ObjectAssembler domainMapper) {
        this.channelService = channelService;
        this.domainMapper = domainMapper;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<ChannelResponse>> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<ChannelEntity> page = channelService.getChannels(text, pageable, false);
        return ResponseEntity.ok().body(domainMapper.fromChannelPageToPagedResponse(page));
    }

    @GetMapping("{channelId}")
    public ResponseEntity<ChannelSummaryResponse> getChannelSummary(@Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        ChannelSummaryResponse channelSummary = channelService.getChannelSummary(channelId);
        if (channelSummary == null) throw channelNotFound(channelId);
        return ResponseEntity.ok().body(channelSummary);
    }

    @PostMapping("")
    public ResponseEntity<ChannelIdRequest> addChannel(@RequestBody @Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        if (channelService.isExistById(channelId)) throw channelExists(channelId);
        channelService.newPendingChannel(channelId);
        log.info("Added a pending channel: {channelId={}}", channelId);
        return ResponseEntity.accepted().body(dto);
    }

    @PutMapping("")
    public ResponseEntity<ChannelIdRequest> updateChannel(@RequestBody @Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        ChannelEntity channelEntity = channelService.getById(channelId);
        if (channelEntity == null) throw channelNotFound(channelId);
        StatusCode statusCode = channelEntity.getContextStatus().getStatusCode();
        if (statusCode == StatusCode.PENDING) throw channelScheduled(channelId);
        if (statusCode == StatusCode.LOCKED_FOR_DELETE) throw channelLockedForDelete(channelId);
        Integer workerId = channelEntity.getWorkerId();
        if (workerId != null) throw channelPassedToWorker(channelId, workerId);
        channelEntity.getContextStatus().setStatusCode(StatusCode.PENDING);
        channelService.save(channelEntity);
        log.info("Channel scheduled for update: " + channelId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{channelId}")
    public ResponseEntity<ChannelIdRequest> deleteChannel(@Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        if (!channelService.isExistById(channelId)) throw channelNotFound(channelId);
        log.info("Deleting a channel: {channelId={}}", channelId);
        channelService.deleteChannel(channelId);
        log.info("Deleted a channel: {channelId={}}", channelId);
        return ResponseEntity.ok().body(dto);
    }

}
