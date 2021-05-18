package com.ctzn.ytsservice.interfaces.rest.exception;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.HttpStatus;

public class ResourceException extends RuntimeException {

    private String entityId;

    private HttpStatus httpStatus;

    public String getEntityId() {
        return entityId;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ResourceException(String entityId, HttpStatus httpStatus, String messagePattern, Object... objects) {
        super(MessageFormatter.arrayFormat(messagePattern, objects).getMessage());
        this.entityId = entityId;
        this.httpStatus = httpStatus;
    }

    public static ResourceException channelNotFound(String channelId) {
        return new ResourceException(channelId, HttpStatus.NOT_FOUND, "Channel is not found: [channelId: {}]", channelId);
    }

    public static ResourceException userNotFound(String channelId) {
        return new ResourceException(channelId, HttpStatus.NOT_FOUND, "Author is not found: [authorChannelId: {}]", channelId);
    }

    public static ResourceException channelExists(String channelId) {
        return new ResourceException(channelId, HttpStatus.CONFLICT, "Channel already exists: [channelId: {}]", channelId);
    }

    public static ResourceException channelScheduled(String channelId) {
        return new ResourceException(channelId, HttpStatus.CONFLICT, "Channel is already scheduled: [channelId: {}]", channelId);
    }

    public static ResourceException channelPassedToWorker(String channelId, Integer workerId) {
        return new ResourceException(channelId, HttpStatus.CONFLICT, "Channel is already passed to a worker: [channelId: {}, workerId: {}]", channelId, workerId);
    }

    public static ResourceException channelLockedForDelete(String channelId) {
        return new ResourceException(channelId, HttpStatus.CONFLICT, "Channel is locked for delete: [channelId: {}]", channelId);
    }

    public static ResourceException videoNotFound(String videoId) {
        return new ResourceException(videoId, HttpStatus.NOT_FOUND, "Video is not found: [videoId: {}]", videoId);
    }

    public static ResourceException videoScheduled(String videoId) {
        return new ResourceException(videoId, HttpStatus.CONFLICT, "Video is already scheduled: [videoId: {}]", videoId);
    }

    public static ResourceException videoPassedToWorker(String videoId, Integer workerId) {
        return new ResourceException(videoId, HttpStatus.CONFLICT, "Video is already passed to a worker: [videoId: {}, workerId: {}]", videoId, workerId);
    }

    public static ResourceException videoLockedForDelete(String videoId) {
        return new ResourceException(videoId, HttpStatus.CONFLICT, "Video is locked for delete: [videoId: {}]", videoId);
    }

}
