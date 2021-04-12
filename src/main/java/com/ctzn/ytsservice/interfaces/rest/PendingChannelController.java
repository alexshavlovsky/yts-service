package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.PendingChannelRequest;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log
public class PendingChannelController {

    private final ChannelRepository channelRepository;

    public PendingChannelController(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @PostMapping("api/channels")
    public ResponseEntity<PendingChannelRequest> addChannel(@RequestBody PendingChannelRequest channelJobRequest) {
        String id = channelJobRequest.getChannelId();
        if (channelRepository.findById(id).isPresent()) {
            log.warning("Channel already exists: " + id);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        log.info("Add a pending channel: " + id);
        channelRepository.save(ChannelEntity.newPendingChannel(id));
        return ResponseEntity.accepted().body(channelJobRequest);
    }

}
