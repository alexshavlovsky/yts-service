package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface CommentRepository extends PagingAndSortingRepository<CommentEntity, String> {

    Page<CommentEntity> findAllByTextContainingIgnoreCase(String text, Pageable pageable);

    Page<CommentEntity> nativeFts(String query, Pageable pageable);

    long countByVideo_Channel_channelId(String channelId);

    long countByVideo_videoId(String videoId);

    Page<CommentEntity> findAllByVideo_videoId(String videoId, Pageable pageable);

    Page<CommentEntity> findAllByVideo_videoIdAndTextContainingIgnoreCase(String videoId, String text, Pageable pageable);

}
