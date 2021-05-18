package com.ctzn.ytsservice.infrastructure.repositories;

import com.ctzn.ytsservice.domain.entities.VideoEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("postgres")
public interface VideoRepositoryPostgres extends VideoRepository {

    @Query(value = "select * from videos where tsv @@ plainto_tsquery(:plain_query)",
            countQuery = "select count(*) from channels where tsv @@ plainto_tsquery(:plain_query)",
            nativeQuery = true)
    Page<VideoEntity> nativeFts(@Param("plain_query") String query, Pageable pageable);

}
