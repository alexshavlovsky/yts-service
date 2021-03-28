package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.scraper.entity.ChannelEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends CrudRepository<ChannelEntity, String> {
}
