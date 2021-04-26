package com.ctzn.ytsservice.domain.entities;

import com.ctzn.youtubescraper.core.persistence.dto.ChannelDTO;
import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@ToString(exclude = "videos")
@Table(name = "channels")
public class ChannelEntity extends Auditable {
    @Id
    @EqualsAndHashCode.Include
    public Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "channel_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ChannelNaturalId naturalId;

    public String channelVanityName;
    public String title;
    public Integer videoCount;
    public Long subscriberCount;
    @OneToMany(mappedBy = "channel")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public List<VideoEntity> videos;
    @Embedded
    ContextStatus contextStatus;

    public String getChannelId() {
        return naturalId.getChannelId();
    }

    public Integer getFetchedVideoCount() {
        return videos.size();
    }

    public String getShortStatus() {
        return contextStatus.statusCode.name();
    }

    public static ChannelEntity fromChannelDTO(ChannelNaturalId publicId, ChannelDTO dto, ContextStatus contextStatus) {
        return new ChannelEntity(
                null,
                publicId,
                dto.getChannelVanityName(),
                dto.getTitle(),
                dto.getVideoCount(),
                dto.getSubscriberCount(),
                Collections.emptyList(),
                contextStatus
        );
    }

    public static ChannelEntity newPendingChannel(ChannelNaturalId publicId) {
        return new ChannelEntity(
                null,
                publicId,
                null,
                publicId.getChannelId(),
                null,
                null,
                Collections.emptyList(),
                new ContextStatus(StatusCode.PENDING)
        );
    }

}
