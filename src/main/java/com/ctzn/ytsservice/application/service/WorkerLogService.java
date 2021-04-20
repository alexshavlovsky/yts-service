package com.ctzn.ytsservice.application.service;

import com.ctzn.ytsservice.domain.entities.WorkerLogEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.WorkerLogRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.WorkerLogResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkerLogService {

    private WorkerLogRepository workerLogRepository;
    private ObjectAssembler objectAssembler;

    public WorkerLogService(WorkerLogRepository workerLogRepository, ObjectAssembler objectAssembler) {
        this.workerLogRepository = workerLogRepository;
        this.objectAssembler = objectAssembler;
    }

    public List<WorkerLogResponse> getByContextId(String id) {
        List<WorkerLogEntity> workerLogEntities = workerLogRepository.getAllByContextId(id);
        return workerLogEntities.stream().map(wl -> objectAssembler.map(wl, WorkerLogResponse.class)).collect(Collectors.toList());
    }

}
