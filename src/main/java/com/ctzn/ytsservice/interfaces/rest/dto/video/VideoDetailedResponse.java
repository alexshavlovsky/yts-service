package com.ctzn.ytsservice.interfaces.rest.dto.video;

import com.ctzn.ytsservice.interfaces.rest.dto.ContextStatusResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoDetailedResponse extends VideoResponse {
    private Date createdDate;
    private Date lastUpdatedDate;
    public ContextStatusResponse contextStatus;
}
