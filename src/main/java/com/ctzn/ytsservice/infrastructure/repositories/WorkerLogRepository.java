package com.ctzn.ytsservice.infrastructure.repositories;

import com.ctzn.ytsservice.domain.entities.WorkerLogEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkerLogRepository extends CrudRepository<WorkerLogEntity, Long> {
    List<WorkerLogEntity> getAllByContextId(String contextId);
}
