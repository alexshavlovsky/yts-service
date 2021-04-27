package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ChannelRepository extends PagingAndSortingRepository<ChannelEntity, Long> {

    Optional<ChannelEntity> findByNaturalId_channelId(String channelId);

    void deleteByNaturalId_channelId(String channelId);

    Page<ChannelEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<ChannelEntity> nativeFts(String query, Pageable pageable);

    //    @EntityGraph(attributePaths = { "naturalId" })
    List<ChannelEntity> findAllByContextStatus_StatusCodeOrderByCreatedDate(StatusCode statusCode);

}
