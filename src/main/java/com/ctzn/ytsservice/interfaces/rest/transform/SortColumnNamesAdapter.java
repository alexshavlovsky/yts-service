package com.ctzn.ytsservice.interfaces.rest.transform;

import org.hibernate.SessionFactory;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class SortColumnNamesAdapter {

    @PersistenceContext
    private EntityManager entityManager;

    public <T> Pageable adapt(Pageable pageable, Class<T> clazz) {
        if (pageable.getSort().isSorted()) {
            SessionFactory sessionFactory;
            if (entityManager == null || (sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class)) == null)
                return pageable;
            AbstractEntityPersister persister = (AbstractEntityPersister) ((MetamodelImplementor) sessionFactory.getMetamodel()).entityPersister(clazz);
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
