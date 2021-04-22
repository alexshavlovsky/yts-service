package com.ctzn.ytsservice.interfaces.rest;


import com.ctzn.ytsservice.interfaces.rest.dto.UserProjection;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("/api/users")
public class UserController {

    private CommentRepository commentRepository;
    private ObjectAssembler domainMapper;

    public UserController(CommentRepository commentRepository, ObjectAssembler domainMapper) {
        this.commentRepository = commentRepository;
        this.domainMapper = domainMapper;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<UserProjection>> getAll(Pageable pageable) {
        Page<UserProjection> page = commentRepository.getUsers(pageable);
        return ResponseEntity.ok().body(domainMapper.fromUserPageToPagedResponse(page));
    }

}
