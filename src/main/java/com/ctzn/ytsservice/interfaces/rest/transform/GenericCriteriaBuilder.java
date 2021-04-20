package com.ctzn.ytsservice.interfaces.rest.transform;

import com.ctzn.ytsservice.interfaces.rest.dto.query.Predicateable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class GenericCriteriaBuilder {

    @PersistenceContext
    EntityManager em;

    private <X> TypedQuery<Long> countQuery(CriteriaBuilder cb, Predicate[] predicates, Class<X> entityClass) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        return em.createQuery(countQuery.select(cb.count(countQuery.from(entityClass))).where(cb.and(predicates)));
    }

    public <X> Page<X> getPage(Predicateable<X> dto, Pageable pageable, Class<X> entityClass) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<X> cq = cb.createQuery(entityClass);
        Root<X> root = cq.from(entityClass);
        Predicate[] predicates = dto.toPredicates(cb, root);
        cq.where(cb.and(predicates));
        cq.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        List<X> result = em.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        return PageableExecutionUtils.getPage(result, pageable, () -> countQuery(cb, predicates, entityClass).getSingleResult());
    }

}
