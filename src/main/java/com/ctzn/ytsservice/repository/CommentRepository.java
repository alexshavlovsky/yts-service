package com.ctzn.ytsservice.repository;

import com.ctzn.ytsservice.domain.entity.CommentEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<CommentEntity, String> {
}
