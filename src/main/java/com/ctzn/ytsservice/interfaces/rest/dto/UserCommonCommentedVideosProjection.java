package com.ctzn.ytsservice.interfaces.rest.dto;

import java.util.List;


public interface UserCommonCommentedVideosProjection {
    String getAuthorChannelId();
    String getAuthorText();
    Long getVideoCount();
    List<String> getVideos();
}
