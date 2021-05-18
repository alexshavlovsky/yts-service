package com.ctzn.ytsservice.infrastructure;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import org.hibernate.dialect.H2Dialect;

public class H2CustomDialect extends H2Dialect {

    public H2CustomDialect() {
        super();
        this.registerHibernateType(2003, StringArrayType.class.getName());
    }

}
