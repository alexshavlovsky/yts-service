package com.ctzn.ytsservice.application.service;

import com.ctzn.youtubescraper.core.persistence.dto.ChannelDTO;
import com.ctzn.youtubescraper.core.persistence.dto.ContextStatusDTO;
import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.ChannelNaturalId;
import com.ctzn.ytsservice.domain.entities.ContextStatus;
import com.ctzn.ytsservice.infrastructure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastructure.repositories.VideoRepository;
import com.ctzn.ytsservice.infrastructure.repositories.comment.AuthorChannelRepository;
import com.ctzn.ytsservice.infrastructure.repositories.comment.AuthorTextRepository;
import com.ctzn.ytsservice.infrastructure.repositories.naturalid.ChannelNaturalIdRepository;
import com.ctzn.ytsservice.infrastructure.repositories.naturalid.CommentNaturalIdRepository;
import com.ctzn.ytsservice.infrastructure.repositories.naturalid.VideoNaturalIdRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelDetailedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChannelService {

    private ChannelRepository channelRepository;
    private ChannelNaturalIdRepository channelNaturalIdRepository;
    private VideoRepository videoRepository;
    private VideoNaturalIdRepository videoNaturalIdRepository;
    private CommentNaturalIdRepository commentNaturalIdRepository;
    private WorkerLogService workerLogService;
    private SortColumnNamesAdapter sortColumnNamesAdapter;
    private ObjectAssembler objectAssembler;
    private AuthorTextRepository authorTextRepository;
    private AuthorChannelRepository authorChannelRepository;

    public ChannelService(ChannelRepository channelRepository, ChannelNaturalIdRepository channelNaturalIdRepository, VideoRepository videoRepository, VideoNaturalIdRepository videoNaturalIdRepository, CommentNaturalIdRepository commentNaturalIdRepository, WorkerLogService workerLogService, SortColumnNamesAdapter sortColumnNamesAdapter, ObjectAssembler objectAssembler, AuthorTextRepository authorTextRepository, AuthorChannelRepository authorChannelRepository) {
        this.channelRepository = channelRepository;
        this.channelNaturalIdRepository = channelNaturalIdRepository;
        this.videoRepository = videoRepository;
        this.videoNaturalIdRepository = videoNaturalIdRepository;
        this.commentNaturalIdRepository = commentNaturalIdRepository;
        this.workerLogService = workerLogService;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
        this.objectAssembler = objectAssembler;
        this.authorTextRepository = authorTextRepository;
        this.authorChannelRepository = authorChannelRepository;
    }

    public ChannelEntity createOrUpdateAndGet(ChannelDTO channelDTO, ContextStatusDTO contextStatusDTO) {
        String channelId = channelDTO.getChannelId();
        ChannelEntity persistentChanel = channelRepository.findByNaturalId_channelId(channelId).orElse(null);
        if (persistentChanel == null) {
            return ChannelEntity.fromChannelDTO(
                    ChannelNaturalId.newFromPublicId(channelId),
                    channelDTO,
                    ContextStatus.fromContextStatusDTO(contextStatusDTO)
            );
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

    public ChannelSummaryResponse getChannelSummary(String channelId) {
        ChannelEntity channelEntity = channelRepository.findByNaturalId_channelId(channelId).orElse(null);
        if (channelEntity == null) return null;
        ChannelDetailedResponse channel = objectAssembler.map(channelEntity, ChannelDetailedResponse.class);
        channel.setDoneVideoCount((int) videoRepository.countByChannel_naturalId_channelIdAndContextStatus_statusCode(channelId, StatusCode.DONE));
        Long totalComments = videoRepository.countComments(channelId);
        return new ChannelSummaryResponse(channel, workerLogService.getByContextId(channelId),
                totalComments == null ? 0 : totalComments.intValue());
    }

    public ChannelEntity save(ChannelEntity channelEntity) {
        return channelRepository.save(channelEntity);
    }

    @Transactional
    public void deleteChannel(String channelId) {
        channelRepository.deleteByNaturalId_channelId(channelId);
        channelNaturalIdRepository.deleteOrphans();
        videoNaturalIdRepository.deleteOrphans();
        commentNaturalIdRepository.deleteOrphans();
        authorTextRepository.deleteOrphans();
        authorChannelRepository.deleteOrphans();
    }

    public ChannelEntity getById(String channelId) {
        return channelRepository.findByNaturalId_channelId(channelId).orElse(null);
    }

    public boolean isExistById(String channelId) {
        return getById(channelId) != null;
    }

    public void newPendingChannel(String channelId) {
        ChannelEntity channelEntity = ChannelEntity.newPendingChannel(ChannelNaturalId.newFromPublicId(channelId));
        channelRepository.save(channelEntity);
    }

    public boolean existOnePendingChannel() {
        return channelRepository.findTop1ByContextStatus_statusCodeAndWorkerIdIsNullOrderByLastUpdatedDate(StatusCode.PENDING) != null;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ChannelEntity lockOnePendingChannel(Integer workerId) {
        ChannelEntity channelEntity = channelRepository.findTop1ByContextStatus_statusCodeAndWorkerIdIsNullOrderByLastUpdatedDate(StatusCode.PENDING);
        if (channelEntity == null) return null;
        channelEntity.setWorkerId(workerId);
        return channelRepository.save(channelEntity);
    }

    public boolean unlockChannel(Long id, int workerId) {
        ChannelEntity channelEntity = channelRepository.findById(id).orElse(null);
        if (channelEntity == null || channelEntity.getWorkerId() != workerId) return false;
        channelEntity.setWorkerId(null);
        channelRepository.save(channelEntity);
        return true;
    }

}
