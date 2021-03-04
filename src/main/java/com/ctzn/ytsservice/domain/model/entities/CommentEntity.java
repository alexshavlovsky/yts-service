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
@Table(name = "comments")
public class CommentEntity {
    @Id
    @EqualsAndHashCode.Include
    public String commentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    public VideoEntity video;
    public String authorText;
    public String channelId;
    public String publishedTimeText;
    @Lob
    public String text;
    public int likeCount;
    public int replyCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public CommentEntity parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    List<CommentEntity> replies;
    public Date createdDate;
    public Date lastUpdatedDate;

    public String getVideo() {
        return video.getVideoId();
    }

    public String getParent() {
        return parent == null ? null : parent.getCommentId();
    }
}
