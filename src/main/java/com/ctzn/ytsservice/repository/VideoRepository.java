package com.ctzn.ytsservice.repository;

import com.ctzn.ytsservice.domain.entity.VideoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends PagingAndSortingRepository<VideoEntity, String> {
}
