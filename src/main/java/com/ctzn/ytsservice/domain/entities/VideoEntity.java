package com.ctzn.ytsservice.domain.entities;

import com.ctzn.youtubescraper.core.persistence.dto.VideoDTO;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@ToString(exclude = "comments")
@Table(name = "videos", indexes = {
        @Index(name = "video_id_nat_id_fk_index", columnList = "video_id"),
        @Index(name = "channel_id_channel_fk_index", columnList = "channel_id")
})
public class VideoEntity extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    public Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "video_id")
    public VideoNaturalId naturalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    ChannelEntity channel;
    String title;
    public String publishedTimeText;
    public Date publishedDate;
    int viewCountText;
    @OneToMany(mappedBy = "video")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<CommentEntity> comments;
    @Embedded
    ContextStatus contextStatus;
    Integer totalCommentCount;
    Integer workerId;

    public String getVideoId() {
        return naturalId.getVideoId();
    }

    public String getChannelId() {
        return channel.getNaturalId().getChannelId();
    }

    public String getChannelTitle() {
        return channel.getTitle();
    }

    public String getShortStatus() {
        return contextStatus.statusCode.name();
    }

    public static VideoEntity fromVideoDTO(VideoNaturalId publicId, VideoDTO dto, ChannelEntity channel, ContextStatus contextStatus) {
        return new VideoEntity(
                publicId.getId(),
                publicId,
                channel,
                dto.getTitle(),
                dto.getPublishedTimeText(),
                dto.getPublishedDate(),
                dto.getViewCountText(),
                Collections.emptyList(),
                contextStatus,
                null,
                null
        );
    }

}
