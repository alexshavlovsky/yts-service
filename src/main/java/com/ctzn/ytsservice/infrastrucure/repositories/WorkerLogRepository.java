package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.scraper.entity.WorkerLogEntity;
import org.springframework.data.repository.CrudRepository;

public interface WorkerLogRepository extends CrudRepository<WorkerLogEntity, Long> {
}
