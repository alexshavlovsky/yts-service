package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.application.service.CommentService;
import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.query.CommentQueryRequest;
import com.ctzn.ytsservice.interfaces.rest.dto.CommentResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.GenericCriteriaBuilder;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private CommentService commentService;
    private ObjectAssembler domainMapper;
    private GenericCriteriaBuilder queryBuilder;

    public CommentController(CommentService commentService, ObjectAssembler domainMapper, GenericCriteriaBuilder queryBuilder) {
        this.commentService = commentService;
        this.domainMapper = domainMapper;
        this.queryBuilder = queryBuilder;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<CommentResponse>> findByQuery(CommentQueryRequest dto, Pageable pageable) {
        Page<CommentEntity> page = dto.getVideoId() != null ?
                queryBuilder.getPage(dto, pageable, CommentEntity.class) :
                commentService.getComments(dto.getText(), pageable, true);
        return ResponseEntity.ok().body(domainMapper.fromCommentPageToPagedResponse(page));
    }

}
