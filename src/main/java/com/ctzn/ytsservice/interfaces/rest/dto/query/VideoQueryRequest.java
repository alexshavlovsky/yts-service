package com.ctzn.ytsservice.interfaces.rest.dto.query;

import com.ctzn.ytsservice.domain.entities.VideoEntity;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class VideoQueryRequest implements Predicateable<VideoEntity> {

    String text;
    String channelId;

    public Predicate[] toPredicates(CriteriaBuilder cb, Root<VideoEntity> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (channelId != null) predicates.add(cb.equal(root.get("channel").get("channelId"), channelId));
        if (text != null) predicates.add(cb.like(cb.lower(root.get("title")), "%" + text.toLowerCase() + "%"));
        return predicates.toArray(Predicate[]::new);
    }

}
