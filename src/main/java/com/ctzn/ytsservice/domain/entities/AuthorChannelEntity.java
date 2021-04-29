package com.ctzn.ytsservice.domain.entities;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "author_channels")
public class AuthorChannelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    public Long id;

    @Column(length = 24, unique = true, updatable = false, nullable = false, columnDefinition = "CHAR(24)")
    String channelId;

}
