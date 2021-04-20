package com.ctzn.ytsservice.interfaces.rest.dto.query;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentQueryRequest implements Predicateable<CommentEntity> {

    String text;
    String videoId;

    public Predicate[] toPredicates(CriteriaBuilder cb, Root<CommentEntity> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (videoId != null) predicates.add(cb.equal(root.get("video").get("videoId"), videoId));
        if (text != null) predicates.add(cb.like(cb.lower(root.get("text")), "%" + text.toLowerCase() + "%"));
        return predicates.toArray(Predicate[]::new);
    }

}
