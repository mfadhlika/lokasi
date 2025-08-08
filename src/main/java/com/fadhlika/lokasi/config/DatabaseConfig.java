package com.fadhlika.lokasi.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.sqlite.SQLiteDataSource;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {
    @Value("${lokasi.data_dir}")
    private String dataDir;

    @Bean
    @Primary
    public DataSource mainDataSource() throws SQLException {
        SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
        sqliteDataSource.setUrl(String.format("jdbc:sqlite:%s/lokasi.db", dataDir));
        sqliteDataSource.setLoadExtension(true);
        sqliteDataSource.setJournalMode("WAL");

        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource(sqliteDataSource);
        ds.setConnectionInitSql("SELECT load_extension('mod_spatialite')");
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
                .locations("classpath:db/migrations")
                .baselineVersion("0")
                .load();

        flyway.baseline();
        flyway.migrate();

        return ds;
    }

    @Bean
    @Qualifier("jobrunrDataSource")
    public DataSource jobrunrDataSource() {
        SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
        sqliteDataSource.setUrl(String.format("jdbc:sqlite:%s/jobrunr.db", dataDir));
        sqliteDataSource.setJournalMode("WAL");

        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource(sqliteDataSource);
        ds.setMinimumIdle(2);
        ds.setMaximumPoolSize(10);
        ds.setIdleTimeout(120000);
        ds.setLeakDetectionThreshold(300000);

        return ds;
    }
}
