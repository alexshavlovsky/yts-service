package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.model.entities.ChannelEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends PagingAndSortingRepository<ChannelEntity, String> {
}
