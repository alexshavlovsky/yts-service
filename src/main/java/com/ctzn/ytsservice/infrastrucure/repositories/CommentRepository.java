package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.model.entities.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<CommentEntity, String> {

    Page<CommentEntity> findAllByTextContainingIgnoreCase(String text, Pageable pageable);

    @Query(value = "SELECT T.* FROM FT_SEARCH_DATA(:word, 0, 0) FT, COMMENTS T WHERE FT.`TABLE`='COMMENTS' AND T.COMMENT_ID=FT.KEYS[1]",
            countQuery = "SELECT COUNT(T.*) FROM FT_SEARCH_DATA(:word, 0, 0) FT, COMMENTS T WHERE FT.`TABLE`='COMMENTS' AND T.COMMENT_ID=FT.KEYS[1]",
            nativeQuery = true)
    Page<CommentEntity> find(@Param("word") String word, Pageable pageable);

}
