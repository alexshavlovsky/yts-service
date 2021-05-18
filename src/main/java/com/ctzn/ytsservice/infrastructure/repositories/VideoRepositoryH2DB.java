package com.ctzn.ytsservice.infrastructure.repositories;

import com.ctzn.ytsservice.domain.entities.VideoEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("h2db")
public interface VideoRepositoryH2DB extends VideoRepository {

    @Query(value = "SELECT T.* FROM FT_SEARCH_DATA(:plain_query, 0, 0) FT, VIDEOS T WHERE FT.`TABLE`='VIDEOS' AND T.VIDEO_ID=FT.KEYS[1]",
            countQuery = "SELECT COUNT(T.*) FROM FT_SEARCH_DATA(:plain_query, 0, 0) FT, VIDEOS T WHERE FT.`TABLE`='VIDEOS' AND T.VIDEO_ID=FT.KEYS[1]",
            nativeQuery = true)
    Page<VideoEntity> nativeFts(@Param("plain_query") String query, Pageable pageable);

}
