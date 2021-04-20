package com.ctzn.ytsservice.application.service;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class CommentService {

    private CommentRepository repository;
    private SortColumnNamesAdapter sortColumnNamesAdapter;

    public CommentService(CommentRepository repository, SortColumnNamesAdapter sortColumnNamesAdapter) {
        this.repository = repository;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
    }

    public Page<CommentEntity> getComments(String rawQuery, Pageable pageable, boolean optimize) {
        boolean noFiltering = rawQuery == null || rawQuery.isEmpty() || rawQuery.isBlank();
        if (optimize) {
            return noFiltering ?
                    repository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    repository.nativeFts(rawQuery, sortColumnNamesAdapter.adapt(pageable, CommentEntity.class));
        } else {
            return noFiltering ?
                    repository.findAll(pageable) :
                    repository.findAllByTextContainingIgnoreCase(rawQuery, pageable);
        }
    }

}
