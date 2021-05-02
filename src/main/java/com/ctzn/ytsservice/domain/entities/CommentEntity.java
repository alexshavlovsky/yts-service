package com.ctzn.ytsservice.domain.entities;

import com.ctzn.youtubescraper.core.persistence.dto.CommentDTO;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "comment_id_nat_id_fk_index", columnList = "comment_id"),
        @Index(name = "video_id_video_fk_index", columnList = "video_id"),
        @Index(name = "author_text_id_author_text_fk_index", columnList = "author_text_id"),
        @Index(name = "author_channel_id_author_channel_fk_index", columnList = "author_channel_id"),
})
public class CommentEntity extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    public Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "comment_id")
    public CommentNaturalId naturalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "video_id")
    public VideoEntity video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_text_id")
    public AuthorTextEntity authorText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_channel_id")
    public AuthorChannelEntity authorChannel;

    public String publishedTimeText;
    public Date publishedDate;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    public String text;
    public int likeCount;
    public int replyCount;

    public String getCommentId() {
        CommentNaturalId nId = getNaturalId();
        return nId.getReplyId() == null ?
                nId.getThreadId() :
                nId.getThreadId() + '.' + nId.getReplyId();
    }

    public String getAuthorText() {
        return authorText.getText();
    }

    public String getAuthorChannelId() {
        return authorChannel.getChannelId();
    }

    public String getVideoId() {
        return video.getNaturalId().getVideoId();
    }

    public String getVideoTitle() {
        return video.getTitle();
    }

    public static CommentEntity fromCommentDTO(CommentNaturalId publicId, VideoEntity videoEntity, AuthorTextEntity authorText, AuthorChannelEntity authorChannel, CommentDTO dto) {
        return new CommentEntity(
                publicId.getId(),
                publicId,
                videoEntity,
                authorText,
                authorChannel,
                dto.getPublishedTimeText(),
                dto.getPublishedDate(),
                dto.getText(),
                dto.getLikeCount(),
                dto.getReplyCount()
        );
    }

}
