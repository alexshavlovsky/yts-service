package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entity.CommentEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("h2db")
public interface CommentRepositoryH2DB extends CommentRepository {

    @Query(value = "SELECT T.* FROM FT_SEARCH_DATA(:plain_query, 0, 0) FT, COMMENTS T WHERE FT.`TABLE`='COMMENTS' AND T.COMMENT_ID=FT.KEYS[1]",
            countQuery = "SELECT COUNT(T.*) FROM FT_SEARCH_DATA(:plain_query, 0, 0) FT, COMMENTS T WHERE FT.`TABLE`='COMMENTS' AND T.COMMENT_ID=FT.KEYS[1]",
            nativeQuery = true)
    Page<CommentEntity> nativeFts(@Param("plain_query") String query, Pageable pageable);

}
