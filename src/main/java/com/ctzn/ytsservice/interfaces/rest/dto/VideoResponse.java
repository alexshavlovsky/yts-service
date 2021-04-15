package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

import java.util.Date;

@Data
public class VideoResponse {
    public String videoId;
    public String channelId;
    public String channelTitle;
    public String title;
    public String publishedTimeText;
    public Date publishedDate;
    public int viewCountText;
    public Integer totalCommentCount;
    public String shortStatus;
}
