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
@Table(name = "videos")
public class VideoEntity {
    @Id
    @EqualsAndHashCode.Include
    String videoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    ChannelEntity channel;
    String title;
    String publishedTimeText;
    int viewCountText;
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    List<CommentEntity> comments;
    public Date createdDate;
    public Date lastUpdatedDate;
}
