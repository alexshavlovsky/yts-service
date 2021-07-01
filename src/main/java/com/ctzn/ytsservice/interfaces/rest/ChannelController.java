package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.youtubescraper.core.persistence.PersistenceRunnerStepBuilder;
import com.ctzn.youtubescraper.core.persistence.dto.StatusCode;
import com.ctzn.ytsservice.application.channelrunner.RunnerFactory;
import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelSummaryResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.ReadableResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.ChannelIdRequest;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.ChannelRunnerConfigDTO;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.ResponseFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ctzn.ytsservice.interfaces.rest.exception.ResourceException.*;

@Slf4j
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final ObjectAssembler domainMapper;
    private final ResponseFormatter responseFormatter;
    private final RunnerFactory commentRunnerFactory;

    public ChannelController(ChannelService channelService, ObjectAssembler domainMapper, ResponseFormatter responseFormatter, RunnerFactory commentRunnerFactory) {
        this.channelService = channelService;
        this.domainMapper = domainMapper;
        this.responseFormatter = responseFormatter;
        this.commentRunnerFactory = commentRunnerFactory;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<ChannelResponse>> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<ChannelEntity> page = channelService.getChannels(text, pageable, false);
        return ResponseEntity.ok().body(domainMapper.fromChannelPageToPagedResponse(page));
    }

    @GetMapping("{channelId}")
    public ResponseEntity<ChannelSummaryResponse> getChannelSummary(@Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        ChannelSummaryResponse channelSummary = channelService.getChannelSummary(channelId);
        if (channelSummary == null) throw channelNotFound(channelId);
        return ResponseEntity.ok().body(channelSummary);
    }

    private String[] verifyAndSerializeConfig(ChannelRunnerConfigDTO dto) {
        // try to serialize / deserialize
        String jsonRunnerConfig = domainMapper.asJson(dto);
        ChannelRunnerConfigDTO restoredDto = domainMapper.parse(jsonRunnerConfig, ChannelRunnerConfigDTO.class);
        // try to create a builder
        PersistenceRunnerStepBuilder.BuildStep builder = commentRunnerFactory.newChannelRunnerBuilder(restoredDto);
        return new String[]{jsonRunnerConfig, builder.toString()};
    }

    @PostMapping("")
    public ResponseEntity<ReadableResponse> addChannel(@RequestBody @Valid ChannelRunnerConfigDTO dto) {
        String channelId = dto.getChannelIdInput();
        if (channelService.isExistById(channelId)) throw channelExists(channelId);
        String[] json = verifyAndSerializeConfig(dto);
        channelService.newPendingChannel(channelId, json[0]);
        return responseFormatter.getResponse(dto.getChannelIdInput(), "Channel scheduled: [channelId: {}, runnerConfig: {}]", channelId, json[1]);
    }

    @PutMapping("")
    public ResponseEntity<ReadableResponse> updateChannel(@RequestBody @Valid ChannelRunnerConfigDTO dto) {
        String channelId = dto.getChannelIdInput();
        ChannelEntity channelEntity = channelService.getById(channelId);
        if (channelEntity == null) throw channelNotFound(channelId);
        StatusCode statusCode = channelEntity.getContextStatus().getStatusCode();
        if (statusCode == StatusCode.PENDING) throw channelScheduled(channelId);
        if (statusCode == StatusCode.LOCKED_FOR_DELETE) throw channelLockedForDelete(channelId);
        Integer workerId = channelEntity.getWorkerId();
        if (workerId != null) throw channelPassedToWorker(channelId, workerId);
        String[] json = verifyAndSerializeConfig(dto);
        channelEntity.getContextStatus().setStatusCode(StatusCode.PENDING);
        channelEntity.getContextStatus().setStatusMessage(json[0]);
        channelService.save(channelEntity);
        return responseFormatter.getResponse(channelId, "Channel scheduled for update:  [channelId: {}, runnerConfig: {}]", channelId, json[1]);
    }

    @DeleteMapping("{channelId}")
    public ResponseEntity<ReadableResponse> deleteChannel(@Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        if (!channelService.isExistById(channelId)) throw channelNotFound(channelId);
        // TODO: set channel status LOCKED_FOR_DELETE before deletion
        channelService.deleteChannel(channelId);
        return responseFormatter.getResponse(channelId, "Channel deleted: [channelId: {}]", channelId);
    }

}
