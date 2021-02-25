package com.ctzn.ytsservice.repository;

import com.ctzn.ytsservice.domain.entity.ChannelEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends PagingAndSortingRepository<ChannelEntity, String> {
}
