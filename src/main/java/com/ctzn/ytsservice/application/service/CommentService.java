package com.ctzn.ytsservice.application.service;

import com.ctzn.youtubescraper.core.persistence.dto.CommentDTO;
import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.domain.entities.CommentNaturalId;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class CommentService {

    private CommentRepository commentRepository;
    private SortColumnNamesAdapter sortColumnNamesAdapter;
    private ObjectAssembler objectAssembler;

    public CommentService(CommentRepository commentRepository, SortColumnNamesAdapter sortColumnNamesAdapter, ObjectAssembler objectAssembler) {
        this.commentRepository = commentRepository;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
        this.objectAssembler = objectAssembler;
    }

    public CommentEntity createOrUpdateAndGet(CommentDTO commentDTO, VideoEntity videoEntity, CommentEntity parentComment) {
        String commentId = commentDTO.getCommentId();
        CommentNaturalId naturalId = CommentNaturalId.newFromPublicId(commentId);
        CommentEntity persistentComment = commentRepository.findByNaturalId_threadIdAndNaturalId_replyId(
                naturalId.getThreadId(), naturalId.getReplyId()).orElse(null);
        if (persistentComment == null) {
            return CommentEntity.fromCommentDTO(naturalId, videoEntity, parentComment, commentDTO);
        } else {
            objectAssembler.map(commentDTO, persistentComment);
            return persistentComment;
        }
    }

    public Page<CommentEntity> getComments(String rawQuery, Pageable pageable, boolean optimize) {
        boolean noFiltering = rawQuery == null || rawQuery.isEmpty() || rawQuery.isBlank();
        if (optimize) {
            return noFiltering ?
                    commentRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    commentRepository.nativeFts(rawQuery, sortColumnNamesAdapter.adapt(pageable, CommentEntity.class));
        } else {
            return noFiltering ?
                    commentRepository.findAll(pageable) :
                    commentRepository.findAllByTextContainingIgnoreCase(rawQuery, pageable);
        }
    }

    public void saveAll(Iterable<CommentEntity> comments) {
        commentRepository.saveAll(comments);
    }

}
