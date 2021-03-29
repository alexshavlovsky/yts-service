package com.ctzn.ytsservice.domain.shared;

import com.ctzn.youtubescraper.model.channelvideos.ChannelDTO;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    public List<VideoEntity> videos;
    public ChannelStatus channelStatus;

    public static ChannelEntity fromChannelDTO(ChannelDTO dto) {
        return new ChannelEntity(
                dto.getChannelId(),
                dto.getChannelVanityName(),
                dto.getTitle(),
                dto.getVideoCount(),
                dto.getSubscriberCount(),
                Collections.emptyList(),
                ChannelStatus.METADATA_FETCHED
        );
    }

    public static ChannelEntity newPendingChannel(String channelId) {
        return new ChannelEntity(
                channelId,
                null,
                null,
                null,
                null,
                Collections.emptyList(),
                ChannelStatus.PENDING
        );
    }

}
