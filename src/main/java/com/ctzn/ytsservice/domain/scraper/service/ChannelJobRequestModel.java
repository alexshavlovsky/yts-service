package com.ctzn.ytsservice.domain.scraper.service;

import lombok.NonNull;
import lombok.Value;

@Value
public class ChannelJobRequestModel {
    @NonNull
    String channelId;
}
