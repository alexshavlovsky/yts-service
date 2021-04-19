package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class VideoIdRequest {
    @NotBlank
    @Size(min = 11, max = 11)
    String videoId;
}
