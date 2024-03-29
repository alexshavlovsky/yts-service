package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChannelSummaryResponse {
    public ChannelDetailedResponse channel;
    public List<WorkerLogResponse> log;
    int totalComments;
}
