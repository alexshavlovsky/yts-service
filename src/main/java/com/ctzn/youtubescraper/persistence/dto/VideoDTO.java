package com.ctzn.youtubescraper.persistence.dto;

import lombok.Value;

@Value
public class VideoDTO {
    String channelId;
    String videoId;
    String title;
    String publishedTimeText;
    int viewCountText;
}
