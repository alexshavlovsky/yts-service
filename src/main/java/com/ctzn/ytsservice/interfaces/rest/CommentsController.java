package com.ctzn.ytsservice.interfaces.rest;

import com.ctzn.ytsservice.domain.scraper.entity.CommentEntity;
import com.ctzn.ytsservice.infrastrucure.repositories.CommentRepository;
import com.ctzn.ytsservice.interfaces.rest.dto.CommentResponse;
import com.ctzn.ytsservice.interfaces.rest.dto.PagedResponse;
import com.ctzn.ytsservice.interfaces.rest.transform.ObjectAssembler;
import org.hibernate.SessionFactory;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    private CommentRepository commentRepository;
    private ObjectAssembler domainMapper;

    public CommentsController(CommentRepository commentRepository, ObjectAssembler domainMapper) {
        this.commentRepository = commentRepository;
        this.domainMapper = domainMapper;
    }

//    Native SQL runner
//    @Autowired
//    private DataSource dataSource;
//
//    @PostConstruct
//    public void runNativeSql() {
//        ClassPathResource resource = new ClassPathResource("full_text_init_h2db.sql");
//        try (Connection connection = dataSource.getConnection()) {
//            ScriptUtils.executeSqlScript(connection, resource);
//        } catch (SQLException | ScriptException e) {
//            e.printStackTrace();
//        }
//    }

    @GetMapping()
    public ResponseEntity<PagedResponse<CommentResponse>> findByTextContaining(@RequestParam(value = "text", required = false) String text, Pageable pageable) {
        Page<CommentEntity> page = text == null || text.isEmpty() || text.isBlank() ?
                // if filtering query param is missing, disable sorting to improve performance
                commentRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                // native full text search
                commentRepository.nativeFts(text, adaptSortColumnNames(pageable));
//                // true full text look up
//                commentRepository.findAllByTextContainingIgnoreCase(text, pageable)
        return ResponseEntity.ok().body(domainMapper.fromPageToPagedResponse(page));
    }

    @PersistenceContext
    private EntityManager entityManager;

    private Pageable adaptSortColumnNames(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            SessionFactory sessionFactory;
            if (entityManager == null || (sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class)) == null)
                return pageable;
            AbstractEntityPersister persister = (AbstractEntityPersister) ((MetamodelImplementor) sessionFactory.getMetamodel()).entityPersister(CommentEntity.class);
            Sort adaptedSort = pageable.getSort().get().limit(1).map(order -> {
                String propertyName = order.getProperty();
                String columnName = persister.getPropertyColumnNames(propertyName)[0];
                return Sort.by(order.getDirection(), columnName);
            }).findFirst().get();
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), adaptedSort);
        }
        return pageable;
    }

}
