package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private ChannelRepository channelRepository;
    private ObjectAssembler domainMapper;
    private SortColumnNamesAdapter sortColumnNamesAdapter;

    public ChannelController(ChannelRepository channelRepository, ObjectAssembler domainMapper, SortColumnNamesAdapter sortColumnNamesAdapter) {
        this.channelRepository = channelRepository;
        this.domainMapper = domainMapper;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<ChannelResponse>> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<ChannelEntity> page = text == null || text.isEmpty() || text.isBlank() ?
                // if filtering query param is missing, disable sorting to improve performance
                channelRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                // native full text search
                channelRepository.nativeFts(text, sortColumnNamesAdapter.adapt(pageable, ChannelEntity.class));
//                // true full text look up
//                commentRepository.findAllByTextContainingIgnoreCase(text, pageable)
        return ResponseEntity.ok().body(domainMapper.fromChannelPageToPagedResponse(page));
    }

}
