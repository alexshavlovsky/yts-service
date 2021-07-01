package com.ctzn.ytsservice.interfaces.rest.dto.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.Duration;

@Data
public class ChannelRunnerConfigDTO {

    private enum ExecutorTimeoutUnit {
        hour, minute;

        Duration getDuration(long value) {
            switch (this) {
                case hour:
                    return Duration.ofHours(value);
                case minute:
                    return Duration.ofMinutes(value);
            }
            return null;
        }
    }

    private enum CommentOrder {
        newest, top
    }

    @NotBlank
    @Size(min = 24, max = 24)
    String channelIdInput;

    @Min(1)
    @Max(20)
    Integer numberOfThreads;

    @Min(1)
    @Max(10000)
    Integer executorTimeoutValue;

    @NotNull
    ExecutorTimeoutUnit executorTimeoutUnit;

    @NotNull
    CommentOrder commentOrder;

    @NotBlank
    @Pattern(regexp = "^Unrestricted$|(^[0-9]+$)")
    String videoLimit;

    @NotBlank
    @Pattern(regexp = "^Unrestricted$|(^[0-9]+$)")
    String commentLimit;

    @NotBlank
    @Pattern(regexp = "^Unrestricted$|(^[0-9]+$)")
    String replyLimit;

    @JsonIgnore
    public Duration getExecutorTimeout() {
        return executorTimeoutUnit.getDuration(executorTimeoutValue);
    }

    @JsonIgnore
    public boolean isOrderNewestFirst() {
        return commentOrder == CommentOrder.newest;
    }

    private static final String UNRESTRICTED = "Unrestricted";

    @JsonIgnore
    public boolean isVideoNoLimit() {
        return UNRESTRICTED.equals(videoLimit);
    }

    @JsonIgnore
    public int getVideoLimitValue() {
        return Integer.parseInt(videoLimit);
    }

    @JsonIgnore
    public boolean isCommentNoLimit() {
        return UNRESTRICTED.equals(commentLimit);
    }

    @JsonIgnore
    public int getCommentLimitValue() {
        return Integer.parseInt(commentLimit);
    }

    @JsonIgnore
    public boolean isReplyNoLimit() {
        return UNRESTRICTED.equals(replyLimit);
    }

    @JsonIgnore
    public int getReplyLimitValue() {
        return Integer.parseInt(replyLimit);
    }

}
