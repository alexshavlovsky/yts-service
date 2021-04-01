package com.ctzn.ytsservice.domain.entities;

import com.ctzn.youtubescraper.persistence.dto.StatusCode;
import com.ctzn.youtubescraper.persistence.dto.VideoDTO;
import lombok.*;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "videos")
public class VideoEntity extends Auditable {
    @Id
    @EqualsAndHashCode.Include
    String videoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    ChannelEntity channel;
    String title;
    String publishedTimeText;
    int viewCountText;
    @OneToMany(mappedBy = "video")
    List<CommentEntity> comments;
    @Embedded
    ContextStatus contextStatus;

    public static VideoEntity fromVideoDTO(VideoDTO dto, ChannelEntity channel) {
        return new VideoEntity(
                dto.getVideoId(),
                channel,
                dto.getTitle(),
                dto.getPublishedTimeText(),
                dto.getViewCountText(),
                Collections.emptyList(),
                new ContextStatus(StatusCode.METADATA_FETCHED)
        );
    }
}