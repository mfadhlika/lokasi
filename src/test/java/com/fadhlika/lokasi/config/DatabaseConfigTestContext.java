package com.fadhlika.lokasi.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.sqlite.SQLiteDataSource;

import com.zaxxer.hikari.HikariDataSource;

@TestConfiguration
public class DatabaseConfigTestContext {

    @Bean
    @Primary
    public DataSource mainDataSource() throws SQLException {
        SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
        sqliteDataSource.setUrl("jdbc:sqlite::memory:");
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
}
