package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.application.service.CommentService;
import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.CommentResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private CommentService commentService;
    private ObjectAssembler domainMapper;

    public CommentController(CommentService commentService, ObjectAssembler domainMapper) {
        this.commentService = commentService;
        this.domainMapper = domainMapper;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<CommentResponse>> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<CommentEntity> page = commentService.getComments(text, pageable, false);
        return ResponseEntity.ok().body(domainMapper.fromCommentPageToPagedResponse(page));
    }

}
