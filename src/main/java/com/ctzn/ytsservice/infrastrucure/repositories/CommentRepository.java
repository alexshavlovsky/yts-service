package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface CommentRepository extends PagingAndSortingRepository<CommentEntity, String> {

    Page<CommentEntity> findAllByTextContainingIgnoreCase(String text, Pageable pageable);

    Page<CommentEntity> nativeFts(String query, Pageable pageable);

    @Query(value = "SELECT count(1) from comments where video_id = ?1", nativeQuery = true)
    Long countComments(String videoId);

}
