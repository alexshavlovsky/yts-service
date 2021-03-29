package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.shared.VideoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends CrudRepository<VideoEntity, String> {
}
