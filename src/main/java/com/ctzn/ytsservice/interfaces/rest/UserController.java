package com.ctzn.ytsservice.interfaces.rest;


import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.infrastructure.repositories.ChannelRepository;
import com.ctzn.ytsservice.infrastructure.repositories.VideoRepository;
import com.ctzn.ytsservice.infrastructure.repositories.comment.CommentRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.*;
import com.ctzn.ytsservice.interfaces.rest.dto.validation.ChannelIdRequest;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.ctzn.ytsservice.interfaces.rest.exception.ResourceException.userNotFound;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final ObjectAssembler domainMapper;

    @GetMapping("{channelId}")
    public ResponseEntity<UserSummaryResponse> getUserDetails(@Valid ChannelIdRequest dto) {
        String channelId = dto.getChannelId();
        UserSummaryProjection user = commentRepository.getUser(channelId);
        if (user == null) throw userNotFound(channelId);
        List<ChannelEntity> commentedChannels = channelRepository.findAllByNaturalId_channelIdIn(user.getCommentedChannels());
        List<VideoEntity> commentedVideos = videoRepository.findAllByNaturalId_videoIdIn(user.getCommentedVideos());
        List<ChannelResponse> channels = commentedChannels.stream().map(c -> domainMapper.map(c, ChannelResponse.class)).collect(Collectors.toList());
        List<VideoResponse> videos = commentedVideos.stream().map(v -> domainMapper.map(v, VideoResponse.class)).collect(Collectors.toList());
        UserSummaryResponse userSummaryResponse = new UserSummaryResponse(
                user.getAuthorChannelId(),
                user.getKnownNames().get(user.getKnownNames().size() - 1),
                user.getKnownNames(),
                channels,
                videos,
                user.getCommentCount(),
                user.getLikeCount(),
                user.getReplyCount(),
                user.getFirstSeen(),
                user.getLastSeen()
        );
        return ResponseEntity.ok().body(userSummaryResponse);
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<UserProjection>> getAll(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<UserProjection> page = text == null ? commentRepository.getUsers(pageable) : commentRepository.getUsers(text, pageable);
        return ResponseEntity.ok().body(domainMapper.fromUserPageToPagedResponse(page));
    }

}
