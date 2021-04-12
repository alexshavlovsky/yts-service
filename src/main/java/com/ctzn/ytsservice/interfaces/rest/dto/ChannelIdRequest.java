package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ChannelIdRequest {
    @NotBlank
    @Size(min = 24, max = 24)
    String channelId;
}
