package com.ctzn.ytsservice.domain.entities;

import com.ctzn.youtubescraper.core.persistence.dto.ChannelDTO;
import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@ToString(exclude = "videos")
@Table(name = "channels", indexes = {
        @Index(name = "channel_id_nat_id_fk_index", columnList = "channel_id")
})
public class ChannelEntity extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    public Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "channel_id")
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
    Integer workerId;

    public String getChannelVanityName() {
        if (channelVanityName == null) return "UNKNOWN";
        try {
            return URLDecoder.decode(channelVanityName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return channelVanityName;
        }
    }

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
                publicId.getId(),
                publicId,
                dto.getChannelVanityName(),
                dto.getTitle(),
                dto.getVideoCount(),
                dto.getSubscriberCount(),
                Collections.emptyList(),
                contextStatus,
                null
        );
    }

    public static ChannelEntity newPendingChannel(ChannelNaturalId publicId) {
        return new ChannelEntity(
                publicId.getId(),
                publicId,
                null,
                publicId.getChannelId(),
                null,
                null,
                Collections.emptyList(),
                new ContextStatus(StatusCode.PENDING),
                null
        );
    }

}
