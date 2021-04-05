package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@NoRepositoryBean
public interface ChannelRepository extends PagingAndSortingRepository<ChannelEntity, String> {
    List<ChannelEntity> findAllByContextStatus_StatusCodeOrderByCreatedDate(StatusCode statusCode);

    Page<ChannelEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<ChannelEntity> nativeFts(String query, Pageable pageable);
}
