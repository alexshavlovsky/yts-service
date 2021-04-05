package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

@Data
public class VideoResponse {
    public String videoId;
    public String channelId;
    public String title;
    public String publishedTimeText;
    public int viewCountText;
    public Integer totalCommentCount;
}
