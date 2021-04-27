package com.ctzn.ytsservice.domain.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "channel_ids")
public class ChannelNaturalId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    Long id;

    @Column(length = 24, unique = true, updatable = false, nullable = false, columnDefinition = "CHAR(24)")
    String channelId;

    public static ChannelNaturalId newFromPublicId(String channelId) {
        return new ChannelNaturalId(null, channelId);
    }

}
