package com.ctzn.ytsservice.interfaces.rest.dto;

import java.util.List;


public interface UserCommonCommentedVideosProjection {
    String getAuthorChannelId();
    String getAuthorText();
    List<String> getRepPosterVideos();
    List<String> getRepRecipientVideos();
    List<String> getSameThreadPosterVideos();
}
