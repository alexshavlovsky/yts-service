package com.ctzn.ytsservice.interfaces.rest.dto;

public interface UserProjection {
    String getUserId();
    String getUserTitle();
    Long getCommentedVideoCount();
    Long getCommentCount();
    Long getLikeCount();
    Long getReplyCount();
}
