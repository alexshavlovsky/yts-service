package com.ctzn.ytsservice.domain.scraper.service;

import com.ctzn.ytsservice.domain.scraper.entity.ChannelEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelJobController {

    private final ChannelRepository channelRepository;

    public ChannelJobController(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @PostMapping("api/jobs")
    public ResponseEntity<ChannelJobRequestModel> saveNote(@RequestBody ChannelJobRequestModel channelJobRequest) {
        String id = channelJobRequest.getChannelId();
        if (channelRepository.findById(id).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        channelRepository.save(ChannelEntity.newPendingChannel(id));
        return ResponseEntity.accepted().body(channelJobRequest);
    }

}
