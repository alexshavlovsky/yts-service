package com.ctzn.ytsservice.domain.entities;

import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

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

    @NaturalId
    @Column(length = 24, unique = true, updatable = false, nullable = false, columnDefinition="CHAR(24)")

    String channelId;

}
