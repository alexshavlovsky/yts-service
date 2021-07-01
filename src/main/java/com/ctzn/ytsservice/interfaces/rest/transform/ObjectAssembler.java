package com.ctzn.ytsservice.interfaces.rest.transform;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.CommentResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.UserProjection;
import com.ctzn.ytsservice.interfaces.rest.dto.video.VideoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ObjectAssembler {

    private ModelMapper modelMapper;
    private static ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public ObjectAssembler() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public <S, D> D map(final S source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }

    public <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }

    public <D> D map(Object source, Type destinationType) {
        return modelMapper.map(source, destinationType);
    }

    // GENERIC TYPE TOKENS
    private static final Type PAGED_COMMENT_RESPONSE = new TypeToken<PagedResponse<CommentResponse>>() {
    }.getType();

    private static final Type PAGED_CHANNEL_RESPONSE = new TypeToken<PagedResponse<ChannelResponse>>() {
    }.getType();

    private static final Type PAGED_VIDEO_RESPONSE = new TypeToken<PagedResponse<VideoResponse>>() {
    }.getType();

    private static final Type PAGED_USER_RESPONSE = new TypeToken<PagedResponse<UserProjection>>() {
    }.getType();

    // GENERIC MAPPERS
    public PagedResponse<CommentResponse> fromCommentPageToPagedResponse(Page<CommentEntity> page) {
        return map(page, PAGED_COMMENT_RESPONSE);
    }

    public PagedResponse<ChannelResponse> fromChannelPageToPagedResponse(Page<ChannelEntity> page) {
        return map(page, PAGED_CHANNEL_RESPONSE);
    }

    public PagedResponse<VideoResponse> fromVideoPageToPagedResponse(Page<VideoEntity> page) {
        return map(page, PAGED_VIDEO_RESPONSE);
    }

    public PagedResponse<UserProjection> fromUserPageToPagedResponse(Page<UserProjection> page) {
        return map(page, PAGED_USER_RESPONSE);
    }

    public String asJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public <T> T parse(String json, Class<T> valueType) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
