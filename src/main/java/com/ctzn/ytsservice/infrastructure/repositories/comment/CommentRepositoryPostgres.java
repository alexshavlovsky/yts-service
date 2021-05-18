package com.ctzn.ytsservice.infrastructure.repositories.comment;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("postgres")
public interface CommentRepositoryPostgres extends CommentRepository {

    @Query(value = "select * from comments where tsv @@ plainto_tsquery(:plain_query)",
            countQuery = "select count(*) from comments where tsv @@ plainto_tsquery(:plain_query)",
            nativeQuery = true)
    Page<CommentEntity> nativeFts(@Param("plain_query") String query, Pageable pageable);

}
