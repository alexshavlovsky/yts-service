package com.ctzn.ytsservice.domain.entities;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "video_ids")
public class VideoNaturalId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    Long id;

    @Column(length = 11, unique = true, updatable = false, nullable = false, columnDefinition = "CHAR(11)")
    String videoId;

    public static VideoNaturalId newFromPublicId(String videoId) {
        return new VideoNaturalId(null, videoId);
    }

}
