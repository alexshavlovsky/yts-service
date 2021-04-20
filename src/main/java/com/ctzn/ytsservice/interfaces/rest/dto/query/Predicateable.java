package com.ctzn.ytsservice.interfaces.rest.dto.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface Predicateable<T> {
    Predicate[] toPredicates(CriteriaBuilder cb, Root<T> root);
}
