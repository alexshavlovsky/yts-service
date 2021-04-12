package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ContextStatusResponse {
    Date statusTimestamp;
    String statusCode;
    String statusMessage;
}
