package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.CommentResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import com.ctzn.ytsservice.interfaces.rest.transform.SortColumnNamesAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private CommentRepository commentRepository;
    private ObjectAssembler domainMapper;
    private SortColumnNamesAdapter sortColumnNamesAdapter;

    public CommentController(CommentRepository commentRepository, ObjectAssembler domainMapper, SortColumnNamesAdapter sortColumnNamesAdapter) {
        this.commentRepository = commentRepository;
        this.domainMapper = domainMapper;
        this.sortColumnNamesAdapter = sortColumnNamesAdapter;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<CommentResponse>> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<CommentEntity> page = text == null || text.isEmpty() || text.isBlank() ?
                // if filtering query param is missing, disable sorting to improve performance
                commentRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                // native full text search
                commentRepository.nativeFts(text, sortColumnNamesAdapter.adapt(pageable, CommentEntity.class));
//                // true full text look up
//                commentRepository.findAllByTextContainingIgnoreCase(text, pageable)
        return ResponseEntity.ok().body(domainMapper.fromCommentPageToPagedResponse(page));
    }

}
