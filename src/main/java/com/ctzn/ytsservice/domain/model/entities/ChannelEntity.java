package com.ctzn.ytsservice.domain.model.entities;


import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "channels")
public class ChannelEntity {
    @Id
    @EqualsAndHashCode.Include
    public String channelId;
    public String channelVanityName;
    public String title;
    public Integer videoCount;
    public Long subscriberCount;
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    public List<VideoEntity> videos;
    public Date createdDate;
    public Date lastUpdatedDate;
}
