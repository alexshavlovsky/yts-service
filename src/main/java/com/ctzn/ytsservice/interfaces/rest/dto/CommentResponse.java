package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {
    public String commentId;
    public String videoId;
    public String authorText;
    public String channelId;
    public String publishedTimeText;
    public String text;
    public int likeCount;
    public int replyCount;
    public String parentId;
    public Date createdDate;
    public Date lastUpdatedDate;
}
