package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Value;

@Value
public class ReadableResponse {
    public String entityId;
    public String status;
    public String message;
}
