package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

@NoRepositoryBean
public interface VideoRepository extends PagingAndSortingRepository<VideoEntity, Long> {

    Optional<VideoEntity> findByNaturalId_videoId(String videoId);

    void  deleteByNaturalId_videoId(String videoId);

    Page<VideoEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<VideoEntity> nativeFts(String query, Pageable pageable);

//    @Query(value = "SELECT count(1) from videos where channel_id = ?1 and status_code = 3", nativeQuery = true)
//    Integer countDoneVideos(String channelId);

    int countByNaturalId_videoIdAndContextStatus_statusCode(String channelId, StatusCode statusCode);

    @Query("SELECT SUM(v.totalCommentCount) FROM VideoEntity v where v.channel.naturalId.channelId = ?1")
    long countComments(String channelId);

//    @Query(value = "SELECT sum(total_comment_count) from videos where channel_id = ?1", nativeQuery = true)
//    Long countComments(String channelId);

}
