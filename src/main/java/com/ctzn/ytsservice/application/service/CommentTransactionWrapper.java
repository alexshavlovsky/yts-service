package com.ctzn.ytsservice.application.service;

import com.ctzn.youtubescraper.core.persistence.dto.CommentDTO;
import com.ctzn.ytsservice.domain.entities.*;
import com.ctzn.ytsservice.infrastrucure.repositories.comment.AuthorChannelRepository;
import com.ctzn.ytsservice.infrastrucure.repositories.comment.AuthorTextRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentTransactionWrapper {

    private AuthorTextRepository authorTextRepository;
    private AuthorChannelRepository authorChannelRepository;

    public CommentTransactionWrapper(AuthorTextRepository authorTextRepository, AuthorChannelRepository authorChannelRepository) {
        this.authorTextRepository = authorTextRepository;
        this.authorChannelRepository = authorChannelRepository;
    }

    //see also https://stackoverflow.com/questions/3562105/jpa-create-if-not-exists-entity

    public CommentEntity createComment(CommentNaturalId naturalId, VideoEntity videoEntity, CommentDTO commentDTO) {
        AuthorTextEntity authorText = authorTextRepository.findByText(commentDTO.getAuthorText()).orElseGet(
                () -> {
                    AuthorTextEntity entity;
                    try {
                        entity = authorTextRepository.save(new AuthorTextEntity(null, commentDTO.getAuthorText()));
                    } catch (Exception ex) {
                        entity = authorTextRepository.findByText(commentDTO.getAuthorText()).orElse(null);
                        if (entity == null) throw ex;
                    }
                    return entity;
                }
        );
        AuthorChannelEntity authorChannel = authorChannelRepository.findByChannelId(commentDTO.getChannelId()).orElseGet(
                () -> {
                    AuthorChannelEntity entity;
                    try {
                        entity = authorChannelRepository.save(new AuthorChannelEntity(null, commentDTO.getChannelId()));
                    } catch (Exception ex) {
                        entity = authorChannelRepository.findByChannelId(commentDTO.getChannelId()).orElse(null);
                        if (entity == null) throw ex;
                    }
                    return entity;
                }
        );
        return CommentEntity.fromCommentDTO(naturalId, videoEntity, authorText, authorChannel, commentDTO);
    }

}
