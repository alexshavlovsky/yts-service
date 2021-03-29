package com.ctzn.youtubescraper.persistence.dto;

import lombok.Value;

@Value
public class WorkerLogDTO {
    String contextId;
    ContextStatusDTO contextStatus;
}
