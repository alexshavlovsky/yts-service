package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.youtubescraper.persistence.dto.StatusCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends CrudRepository<ChannelEntity, String> {
    List<ChannelEntity> findAllByContextStatus_StatusCodeOrderByCreatedDate(StatusCode statusCode);
}
