package com.ctzn.ytsservice.interfaces.rest.dto.video;

public interface VideoStatProjection {
    Long getTotalCommentCount();
    Long getReplyCount();
    Long getUniqueAuthorsCount();
}
