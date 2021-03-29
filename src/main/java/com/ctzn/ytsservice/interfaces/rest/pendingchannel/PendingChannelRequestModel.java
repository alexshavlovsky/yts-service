package com.ctzn.ytsservice.interfaces.rest.pendingchannel;

import lombok.NonNull;
import lombok.Value;

@Value
public class PendingChannelRequestModel {
    @NonNull
    String channelId;
}
