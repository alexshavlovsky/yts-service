package com.ctzn.ytsservice.infrastructure.repositories;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("h2db")
public interface ChannelRepositoryH2DB extends ChannelRepository {

    @Query(value = "SELECT T.* FROM FT_SEARCH_DATA(:plain_query, 0, 0) FT, CHANNELS T WHERE FT.`TABLE`='CHANNELS' AND T.CHANNEL_ID=FT.KEYS[1]",
            countQuery = "SELECT COUNT(T.*) FROM FT_SEARCH_DATA(:plain_query, 0, 0) FT, CHANNELS T WHERE FT.`TABLE`='CHANNELS' AND T.COMMENT_ID=FT.KEYS[1]",
            nativeQuery = true)
    Page<ChannelEntity> nativeFts(@Param("plain_query") String query, Pageable pageable);

}
