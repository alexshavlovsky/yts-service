package com.ctzn.ytsservice.interfaces.rest.dto;

import java.util.Date;

public interface UserProjection {
    String getAuthorChannelId();
    String getAuthorText();
    Long getCommentedChannelCount();
    Long getCommentedVideoCount();
    Long getCommentCount();
    Long getLikeCount();
    Long getReplyCount();
    Date getFirstSeen();
    Date getLastSeen();
}
