package com.ctzn.ytsservice.application.service;

import com.ctzn.youtubescraper.core.persistence.dto.CommentDTO;
import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.domain.entities.CommentNaturalId;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.comment.CommentRepository;
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
    private CommentTransactionWrapper commentTransactionWrapper;

    public CommentService(CommentRepository commentRepository, SortColumnNamesAdapter sortColumnNamesAdapter, ObjectAssembler objectAssembler, CommentTransactionWrapper commentTransactionWrapper) {
        this.commentRepository = commentRepository;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
        this.objectAssembler = objectAssembler;
        this.commentTransactionWrapper = commentTransactionWrapper;
    }

    public CommentEntity createOrUpdateAndGet(CommentDTO commentDTO, VideoEntity videoEntity) {
        String commentId = commentDTO.getCommentId();
        CommentNaturalId naturalId = CommentNaturalId.newFromPublicId(commentId);
        CommentEntity persistentComment = commentRepository.findByNaturalId_threadIdAndNaturalId_replyId(
                naturalId.getThreadId(), naturalId.getReplyId()).orElse(null);
        if (persistentComment == null) {
            return commentTransactionWrapper.createComment(naturalId, videoEntity, commentDTO);
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
