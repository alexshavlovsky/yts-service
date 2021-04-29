package com.ctzn.ytsservice.domain.entities;

import com.ctzn.youtubescraper.core.persistence.dto.CommentDTO;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "comments")
public class CommentEntity extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    public Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CommentNaturalId naturalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    public VideoEntity video;
    public String authorText;
    public String channelId;
    public String publishedTimeText;
    public Date publishedDate;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    public String text;
    public int likeCount;
    public int replyCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public CommentEntity parent;
    @OneToMany(mappedBy = "parent")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<CommentEntity> replies;

    public String getCommentId() {
        CommentNaturalId nId = getNaturalId();
        return nId.getReplyId() == null ?
                nId.getThreadId() :
                nId.getThreadId() + '.' + nId.getReplyId();
    }

    public String getVideoId() {
        return video.getNaturalId().getVideoId();
    }

    public String getVideoTitle() {
        return video.getTitle();
    }

    public String getParentId() {
        return parent == null ? null : parent.getNaturalId().getThreadId();
    }

    public static CommentEntity fromCommentDTO(CommentNaturalId publicId, VideoEntity videoEntity, CommentEntity parentComment, CommentDTO dto) {
        return new CommentEntity(
                publicId.getId(),
                publicId,
                videoEntity,
                dto.getAuthorText(),
                dto.getChannelId(),
                dto.getPublishedTimeText(),
                dto.getPublishedDate(),
                dto.getText(),
                dto.getLikeCount(),
                dto.getReplyCount(),
                parentComment,
                Collections.emptyList()
        );
    }

}
