package com.ctzn.ytsservice.application.service;

import com.ctzn.youtubescraper.core.persistence.dto.ChannelDTO;
import com.ctzn.youtubescraper.core.persistence.dto.ContextStatusDTO;
import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.ChannelNaturalId;
import com.ctzn.ytsservice.domain.entities.ContextStatus;
import com.ctzn.ytsservice.infrastrucure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.VideoRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelDetailedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChannelService {

    private ChannelRepository channelRepository;
    private VideoRepository videoRepository;
    private WorkerLogService workerLogService;
    private SortColumnNamesAdapter sortColumnNamesAdapter;
    private ObjectAssembler objectAssembler;

    public ChannelService(ChannelRepository channelRepository, VideoRepository videoRepository, WorkerLogService workerLogService, SortColumnNamesAdapter sortColumnNamesAdapter, ObjectAssembler objectAssembler) {
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.workerLogService = workerLogService;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
        this.objectAssembler = objectAssembler;
    }

    public ChannelEntity createOrUpdateAndGet(ChannelDTO channelDTO, ContextStatusDTO contextStatusDTO) {
        String channelId = channelDTO.getChannelId();
        ChannelEntity persistentChanel = channelRepository.findByNaturalId_channelId(channelId).orElse(null);
        if (persistentChanel == null) {
            ChannelEntity transientChanel = ChannelEntity.fromChannelDTO(
                    new ChannelNaturalId(null, channelId),
                    channelDTO,
                    ContextStatus.fromContextStatusDTO(contextStatusDTO)
            );
            return transientChanel;
        } else {
            objectAssembler.map(channelDTO, persistentChanel);
            objectAssembler.map(contextStatusDTO, persistentChanel.getContextStatus());
            return persistentChanel;
        }
    }

    public Page<ChannelEntity> getChannels(String rawQuery, Pageable pageable, boolean optimize) {
        boolean noFiltering = rawQuery == null || rawQuery.isEmpty() || rawQuery.isBlank();
        if (optimize) {
            return noFiltering ?
                    channelRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    channelRepository.nativeFts(rawQuery, sortColumnNamesAdapter.adapt(pageable, ChannelEntity.class));
        } else {
            return noFiltering ?
                    channelRepository.findAll(pageable) :
                    channelRepository.findAllByTitleContainingIgnoreCase(rawQuery, pageable);
        }
    }

    public void deleteChannel(String channelId) {
        channelRepository.deleteByNaturalId_channelId(channelId);
    }

    public ChannelSummaryResponse getChannelSummary(String channelId) {
        ChannelEntity channelEntity = channelRepository.findByNaturalId_channelId(channelId).orElse(null);
        if (channelEntity == null) return null;
        ChannelDetailedResponse channel = objectAssembler.map(channelEntity, ChannelDetailedResponse.class);
        channel.setDoneVideoCount(videoRepository.countByNaturalId_videoIdAndContextStatus_statusCode(channelId, StatusCode.DONE));
        //int totalComments = (int) commentRepository.countByVideo_Channel_channelId(channelId); // join and count version (slower version)
        long totalComments = videoRepository.countComments(channelId); // aggregate version (faster version)
        return new ChannelSummaryResponse(channel, workerLogService.getByContextId(channelId), (int) totalComments);
    }

    public ChannelEntity save(ChannelEntity channelEntity) {
        return channelRepository.save(channelEntity);
    }

    public ChannelEntity getById(String channelId) {
        return channelRepository.findByNaturalId_channelId(channelId).orElse(null);
    }

    public boolean isChannelExist(String channelId) {
        return channelRepository.findByNaturalId_channelId(channelId).isPresent();
    }

    public void newPendingChannel(String channelId) {
        ChannelEntity channelEntity = ChannelEntity.newPendingChannel(new ChannelNaturalId(null, channelId));
        channelRepository.save(channelEntity);
    }

    public List<ChannelEntity> getPendingList() {
        return channelRepository.findAllByContextStatus_StatusCodeOrderByCreatedDate(StatusCode.PENDING);
    }

}
