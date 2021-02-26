package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.model.entities.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<CommentEntity, String> {

    Page<CommentEntity> findAllByTextContainingIgnoreCase(String text, Pageable pageable);

}
