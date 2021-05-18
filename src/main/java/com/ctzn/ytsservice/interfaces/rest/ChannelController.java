package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ReadableResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.ChannelIdRequest;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.ResponseFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ctzn.ytsservice.interfaces.rest.exception.ResourceException.*;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final ObjectAssembler domainMapper;
    private final ResponseFormatter responseFormatter;

    public ChannelController(ChannelService channelService, ObjectAssembler domainMapper, ResponseFormatter responseFormatter) {
        this.channelService = channelService;
        this.domainMapper = domainMapper;
        this.responseFormatter = responseFormatter;
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
    public ResponseEntity<ReadableResponse> addChannel(@RequestBody @Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        if (channelService.isExistById(channelId)) throw channelExists(channelId);
        channelService.newPendingChannel(channelId);
        return responseFormatter.getResponse(channelId, "Channel scheduled: [channelId: {}]", channelId);
    }

    @PutMapping("")
    public ResponseEntity<ReadableResponse> updateChannel(@RequestBody @Valid ChannelIdRequest dto) {
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
        return responseFormatter.getResponse(channelId, "Channel scheduled for update:  [channelId: {}]", channelId);
    }

    @DeleteMapping("{channelId}")
    public ResponseEntity<ReadableResponse> deleteChannel(@Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        if (!channelService.isExistById(channelId)) throw channelNotFound(channelId);
        // TODO: set channel status LOCKED_FOR_DELETE before deletion
        channelService.deleteChannel(channelId);
        return responseFormatter.getResponse(channelId, "Channel deleted: [channelId: {}]", channelId);
    }

}
