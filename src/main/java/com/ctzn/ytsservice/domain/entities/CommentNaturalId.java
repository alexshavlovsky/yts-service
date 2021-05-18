package com.ctzn.ytsservice.domain.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "comment_ids",
        uniqueConstraints = @UniqueConstraint(columnNames = {"threadId", "replyId"})
//        indexes = @Index(name = "composite_id_index", columnList = "threadId,replyId")
)
public class CommentNaturalId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    Long id;

    @Column(length = 26, updatable = false, nullable = false, columnDefinition = "CHAR(26)")
    String threadId;

    @Column(length = 22, updatable = false, columnDefinition = "CHAR(22)")
    String replyId;

    public static CommentNaturalId newFromPublicId(String commentId) {
        int len = commentId.length();
        boolean isDotted = commentId.contains(".");
        if (!isDotted && (len == 26 || len == 20)) return new CommentNaturalId(null, commentId, null);
        if (isDotted && (len == (26 + 22 + 1) || len == (20 + 22 + 1))) {
            String[] els = commentId.split("\\.");
            if ((els.length) == 2 && (els[0].length() == 26 || els[0].length() == 20))
                return new CommentNaturalId(null, els[0], els[1]);
        }
        throw new IllegalArgumentException("Invalid comment Id");
    }

}
