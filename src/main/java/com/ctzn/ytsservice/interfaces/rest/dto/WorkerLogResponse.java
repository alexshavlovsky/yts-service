package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

@Data
public class WorkerLogResponse {
    public Long id;
    public ContextStatusResponse contextStatus;
}
