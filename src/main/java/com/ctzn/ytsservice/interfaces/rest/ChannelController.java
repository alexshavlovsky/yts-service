package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelIdRequest;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/channels")
@Log
public class ChannelController {

    private ChannelService channelService;
    private ChannelRepository channelRepository;
    private ObjectAssembler domainMapper;

    public ChannelController(ChannelService channelService, ChannelRepository channelRepository, ObjectAssembler domainMapper) {
        this.channelService = channelService;
        this.channelRepository = channelRepository;
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
        if (channelSummary == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(channelSummary);
    }

    @PostMapping("")
    public ResponseEntity<ChannelIdRequest> addChannel(@RequestBody @Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        if (channelRepository.findById(channelId).isPresent()) {
            log.warning("Channel already exists: " + channelId);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        log.info("Add a pending channel: " + channelId);
        channelRepository.save(ChannelEntity.newPendingChannel(channelId));
        return ResponseEntity.accepted().body(dto);
    }

    @DeleteMapping("{channelId}")
    public ResponseEntity<ChannelIdRequest> deleteChannel(@Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        log.info("Delete a channel: " + channelId);
        channelService.deleteChannel(channelId);
        log.info("OK deleting a channel: " + channelId);
        return ResponseEntity.ok().body(dto);
    }

}
