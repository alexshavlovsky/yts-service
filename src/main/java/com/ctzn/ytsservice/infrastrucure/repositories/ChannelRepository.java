package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.shared.ChannelEntity;
import com.ctzn.ytsservice.domain.shared.ChannelStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends CrudRepository<ChannelEntity, String> {
    List<ChannelEntity> findAllByChannelStatusOrderByCreatedDate(ChannelStatus channelStatus);
}
