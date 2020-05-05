package com.abclinic.server.config;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.IntegerType;

/**
 * @author tmduc
 * @package com.abclinic.server.config
 * @created 5/5/2020 10:42 AM
 */
public class MySQLDialect extends org.hibernate.dialect.MySQLDialect {
    public MySQLDialect() {
        super();
        registerFunction("bitand", new SQLFunctionTemplate(IntegerType.INSTANCE, "(?1 & ?2)"));
    }
}
