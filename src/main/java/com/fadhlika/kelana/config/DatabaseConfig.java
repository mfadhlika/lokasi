package com.fadhlika.kelana.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {
    @Value("${kelana.db_name}")
    private String dbName;

    @Value("${kelana.db_host}")
    private String dbHost;

    @Value("${kelana.db_port}")
    private String dbPort;

    @Value("${kelana.db_user}")
    private String dbUser;

    @Value("${kelana.db_password}")
    private String dbPassword;

    @Value("${spring.flyway.clean-disabled:false}")
    private boolean cleanDisabled;

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    public DataSource mainDataSource() throws SQLException {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?tcpKeepAlive=true", dbHost, dbPort, dbName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);

        HikariDataSource ds = new HikariDataSource(config);

        return ds;
    }
}
