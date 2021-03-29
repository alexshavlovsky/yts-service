package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class PendingChannelRequest {
    @NonNull
    String channelId;
}
