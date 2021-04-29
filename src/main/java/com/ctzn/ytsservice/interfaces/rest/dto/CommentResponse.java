package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {
    public String commentId;
    public String videoId;
    public String videoTitle;
    public String authorText;
    public String authorChannelId;
    public String publishedTimeText;
    public Date publishedDate;
    public String text;
    public int likeCount;
    public int replyCount;
    public Date createdDate;
    public Date lastUpdatedDate;
}
