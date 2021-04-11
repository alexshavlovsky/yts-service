package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private ChannelService channelService;
    private ObjectAssembler domainMapper;

    public ChannelController(ChannelService channelService, ObjectAssembler domainMapper) {
        this.channelService = channelService;
        this.domainMapper = domainMapper;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<ChannelResponse>> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<ChannelEntity> page = channelService.getChannels(text, pageable, true);
        return ResponseEntity.ok().body(domainMapper.fromChannelPageToPagedResponse(page));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteChannel(@PathVariable("id") String channelId) {
        // TODO improve performance of this call
        channelService.deleteChannel(channelId);
        return ResponseEntity.ok().body(channelId);
    }

}
