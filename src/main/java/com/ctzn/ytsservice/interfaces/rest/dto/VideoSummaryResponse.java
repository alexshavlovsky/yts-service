package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VideoSummaryResponse {
    public VideoDetailedResponse video;
    public List<WorkerLogResponse> log;
    int totalComments;
    // TODO include reply count
}
