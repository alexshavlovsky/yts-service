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
    public String channelId;
    public String channelVanityName;
    public String title;
    public Integer videoCount;
    public Long subscriberCount;
    @OneToMany(mappedBy = "channel")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public List<VideoEntity> videos;
    @Embedded
    ContextStatus contextStatus;

    public Integer getFetchedVideoCount() {
        return videos.size();
    }

    public String getShortStatus() {
        return contextStatus.statusCode.name();
    }

    public static ChannelEntity fromChannelDTO(ChannelDTO dto, ContextStatus contextStatus) {
        return new ChannelEntity(
                dto.getChannelId(),
                dto.getChannelVanityName(),
                dto.getTitle(),
                dto.getVideoCount(),
                dto.getSubscriberCount(),
                Collections.emptyList(),
                contextStatus
        );
    }

    public static ChannelEntity newPendingChannel(String channelId) {
        return new ChannelEntity(
                channelId,
                channelId,
                null,
                null,
                null,
                Collections.emptyList(),
                new ContextStatus(StatusCode.PENDING)
        );
    }

}
