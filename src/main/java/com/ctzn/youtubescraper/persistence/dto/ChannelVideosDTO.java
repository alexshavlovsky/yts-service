package com.ctzn.youtubescraper.persistence.dto;

import lombok.Value;

import java.util.List;

@Value
public class ChannelVideosDTO {
    ChannelDTO channel;
    List<VideoDTO> videos;
}
