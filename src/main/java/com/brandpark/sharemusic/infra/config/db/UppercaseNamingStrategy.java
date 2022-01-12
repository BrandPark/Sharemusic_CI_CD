package com.brandpark.sharemusic.infra.config.db;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

import java.util.Locale;

public class UppercaseNamingStrategy extends SpringPhysicalNamingStrategy {
    @Override
    protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
        return new Identifier((name.toUpperCase(Locale.ROOT)), quoted);
    }
}
