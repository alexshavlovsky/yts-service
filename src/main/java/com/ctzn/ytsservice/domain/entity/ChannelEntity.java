package com.ctzn.ytsservice.domain.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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
    //    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    //    public List<VideoEntity> videos;
    public Date createdDate;
    public Date lastUpdatedDate;
}
