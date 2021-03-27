package com.ctzn.ytsservice.domain.entity;

import com.ctzn.youtubescraper.model.channelvideos.ChannelDTO;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

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

    public static ChannelEntity fromChannelDTO(ChannelDTO dto) {
        return new ChannelEntity(
                dto.getChannelId(),
                dto.getChannelVanityName(),
                dto.getTitle(),
                dto.getVideoCount(),
                dto.getSubscriberCount(),
                Collections.emptyList()
        );
    }
}
