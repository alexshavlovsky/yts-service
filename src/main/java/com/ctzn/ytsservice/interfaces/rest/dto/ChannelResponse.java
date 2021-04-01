package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

@Data
public class ChannelResponse {
    public String channelId;
    public String channelVanityName;
    public String title;
    public int videoCount;
    public long subscriberCount;
}
