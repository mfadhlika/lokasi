package com.fadhlika.lokasi.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.sqlite.SQLiteDataSource;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {
    @Value("${lokasi.db_path}")
    private String dbPath;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    public DataSource mainDataSource() throws SQLException {
        SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
        sqliteDataSource.setUrl(String.format("jdbc:sqlite:%s", dbPath));
        sqliteDataSource.setLoadExtension(true);
        sqliteDataSource.setJournalMode("WAL");

        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource(sqliteDataSource);
        ds.setConnectionInitSql("""
                SELECT load_extension('mod_spatialite');
                PRAGMA trusted_schema=1;
                    """);
        ds.setMinimumIdle(2);
        ds.setMaximumPoolSize(10);
        ds.setIdleTimeout(120000);
        ds.setLeakDetectionThreshold(300000);

        try (Connection conn = ds.getConnection();
                final PreparedStatement stmt = conn
                        .prepareStatement("SELECT InitSpatialMetadata(1)")) {
            stmt.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }

        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .baselineVersion("0")
                .load();

        flyway.baseline();
        flyway.migrate();

        return ds;
    }
}
