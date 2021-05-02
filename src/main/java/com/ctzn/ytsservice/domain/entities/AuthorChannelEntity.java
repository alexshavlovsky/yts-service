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

    // TODO consider allowing null values
    // sometimes youtube returns null channelId (for example if chanel is deleted but comments and author name are still accessible)
    @Column(length = 24, unique = true, updatable = false, nullable = false, columnDefinition = "CHAR(24)")
    String channelId;

}
