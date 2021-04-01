package com.ctzn.ytsservice.interfaces.rest.transform;

import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.ChannelResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.CommentResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ObjectAssembler {

    private ModelMapper modelMapper;

    public ObjectAssembler() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
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

    // GENERIC MAPPERS
    public PagedResponse<CommentResponse> fromCommentPageToPagedResponse(Page<CommentEntity> page) {
        return map(page, PAGED_COMMENT_RESPONSE);
    }

    public PagedResponse<ChannelResponse> fromChannelPageToPagedResponse(Page<ChannelEntity> page) {
        return map(page, PAGED_CHANNEL_RESPONSE);
    }

}
