package com.ctzn.ytsservice.infrastrucure.repositories.comment;

import com.ctzn.ytsservice.domain.entities.AuthorChannelEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorChannelRepository extends CrudRepository<AuthorChannelEntity, Long> {

    Optional<AuthorChannelEntity> findByChannelId(String channelId);

    @Modifying
    @Query(value = "delete from author_channels where id in (select ac.id from author_channels as ac LEFT JOIN comments as c on ac.id = c.author_channel_id where c.author_channel_id is null)",
            nativeQuery = true)
    void deleteOrphans();

}
