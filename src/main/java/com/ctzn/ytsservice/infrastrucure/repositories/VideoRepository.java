package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.model.entities.VideoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends PagingAndSortingRepository<VideoEntity, String> {
}
