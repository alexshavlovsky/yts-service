package com.ctzn.ytsservice.infrastructure;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import org.hibernate.dialect.PostgreSQL10Dialect;

public class PostgreSQL10CustomDialect extends PostgreSQL10Dialect {

    public PostgreSQL10CustomDialect() {
        super();
        this.registerHibernateType(2003, StringArrayType.class.getName());
    }

}
