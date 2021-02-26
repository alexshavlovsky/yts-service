package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.domain.model.entities.CommentEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    private CommentRepository commentRepository;

    public CommentsController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping()
    public Page<CommentEntity> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        return text == null || text.isEmpty() || text.isBlank() ?
                // to improve performance if filtering query param is missing, disable sorting
                commentRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                commentRepository.findAllByTextContainingIgnoreCase(text, pageable);
    }

}
