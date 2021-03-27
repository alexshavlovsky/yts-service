package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entity.WorkerLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerLogRepository extends JpaRepository<WorkerLogEntity, Long> {
}
