package com.ctzn.ytsservice.interfaces.rest.dto;

import com.ctzn.ytsservice.interfaces.rest.dto.video.VideoResponse;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
public class UserSummaryResponse {
    String authorChannelId;
    String authorText;
    List<String> knownNames;
    List<ChannelResponse> commentedChannels;
    List<VideoResponse> commentedVideos;
    Long commentCount;
    Long likeCount;
    Long replyCount;
    Date firstSeen;
    Date lastSeen;
    List<UserCommonCommentedVideosProjection> commentIntersections;
}
