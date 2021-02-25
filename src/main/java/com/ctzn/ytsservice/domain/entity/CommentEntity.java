package com.ctzn.ytsservice.domain.entity;


import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id
    @EqualsAndHashCode.Include
    public String commentId;
    @ManyToOne()
    @JoinColumn(name = "video_id")
    public VideoEntity video;
    public String authorText;
    public String channelId;
    public String publishedTimeText;
    @Lob
    public String text;
    public int likeCount;
    public int replyCount;
    @ManyToOne()
    @JoinColumn(name = "parent_id")
    public CommentEntity parent;
    //    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    //    List<CommentEntity> replies;
    public Date createdDate;
    public Date lastUpdatedDate;
}
