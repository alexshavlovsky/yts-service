package com.ctzn.ytsservice.interfaces.rest.dto;

import java.util.Date;
import java.util.List;


public interface UserSummaryProjection {
    String getAuthorChannelId();
    List<String> getKnownNames();
    List<String> getCommentedChannels();
    List<String> getCommentedVideos();
    Long getCommentCount();
    Long getLikeCount();
    Long getReplyCount();
    Date getFirstSeen();
    Date getLastSeen();
}
