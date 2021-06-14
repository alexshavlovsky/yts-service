package com.ctzn.ytsservice.interfaces.rest.dto.video;

import com.ctzn.ytsservice.interfaces.rest.dto.WorkerLogResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VideoSummaryResponse {
    public VideoDetailedResponse video;
    public List<WorkerLogResponse> log;
    public VideoStatProjection stat;
}
