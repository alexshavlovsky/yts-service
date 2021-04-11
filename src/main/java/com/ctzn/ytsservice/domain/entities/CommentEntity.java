package com.ctzn.ytsservice.domain.entities;

import com.ctzn.youtubescraper.core.persistence.dto.CommentDTO;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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
    public String commentId;
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
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    List<CommentEntity> replies;

    public String getVideoId() {
        return video.getVideoId();
    }

    public String getVideoTitle() {
        return video.getTitle();
    }

    public String getParentId() {
        return parent == null ? null : parent.getCommentId();
    }

    public static CommentEntity fromCommentDTO(VideoEntity videoEntity, CommentEntity parentComment, CommentDTO dto) {
        return new CommentEntity(
                dto.getCommentId(),
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

    public static Map<String, CommentEntity> getCommentMap(VideoEntity videoEntity, List<CommentDTO> comments) {
        return comments.stream().map(comment -> fromCommentDTO(videoEntity, null, comment))
                .collect(LinkedHashMap::new, (map, comment) -> map.put(comment.getCommentId(), comment), Map::putAll);
    }

    public static List<CommentEntity> getReplyList(VideoEntity videoEntity, List<CommentDTO> replies, Map<String, CommentEntity> commentEntityMap) {
        return replies.stream().map(comment -> fromCommentDTO(videoEntity, commentEntityMap.get(comment.parentCommentId), comment))
                .collect(Collectors.toList());
    }

}
