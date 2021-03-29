package com.ctzn.ytsservice.interfaces.rest.pendingchannel;

import com.ctzn.ytsservice.domain.shared.ChannelEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PendingChannelController {

    private final ChannelRepository channelRepository;

    public PendingChannelController(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @PostMapping("api/channels")
    public ResponseEntity<PendingChannelRequestModel> addChannel(@RequestBody PendingChannelRequestModel channelJobRequest) {
        String id = channelJobRequest.getChannelId();
        if (channelRepository.findById(id).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        channelRepository.save(ChannelEntity.newPendingChannel(id));
        return ResponseEntity.accepted().body(channelJobRequest);
    }

}
