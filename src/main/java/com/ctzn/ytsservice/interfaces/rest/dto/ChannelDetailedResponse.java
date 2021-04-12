package com.ctzn.ytsservice.interfaces.rest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChannelDetailedResponse extends ChannelResponse {
    private Date createdDate;
    private Date lastUpdatedDate;
    public Integer fetchedVideoCount;
    public ContextStatusResponse contextStatus;
}
