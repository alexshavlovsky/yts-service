package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface VideoRepository extends PagingAndSortingRepository<VideoEntity, Long> {

    Optional<VideoEntity> findByNaturalId_videoId(String videoId);

    void deleteByNaturalId_videoId(String videoId);

    Page<VideoEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<VideoEntity> nativeFts(String query, Pageable pageable);

    long countByChannel_naturalId_channelIdAndContextStatus_statusCode(String channelId, StatusCode statusCode);

    @Query("SELECT SUM(v.totalCommentCount) FROM VideoEntity v where v.channel.naturalId.channelId = ?1")
    Long countComments(String channelId);

    @Modifying
    @Transactional
    @Query(value = "update videos set worker_id = null",
            nativeQuery = true)
    void resetLocks();

    VideoEntity findTop1ByContextStatus_statusCodeAndWorkerIdIsNullOrderByCreatedDate(StatusCode statusCode);

}
