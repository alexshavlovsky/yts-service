package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.VideoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface VideoRepository extends PagingAndSortingRepository<VideoEntity, String> {

    Page<VideoEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<VideoEntity> nativeFts(String query, Pageable pageable);

}
