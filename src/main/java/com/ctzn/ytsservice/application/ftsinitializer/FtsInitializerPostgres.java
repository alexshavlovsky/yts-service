package com.ctzn.ytsservice.application.ftsinitializer;

import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@Profile("postgres")
@Log
public class FtsInitializerPostgres implements FtsInitializer {

    private DataSource dataSource;
    private NativeSqlScriptExecutor nativeSqlScriptExecutor;

    public FtsInitializerPostgres(DataSource dataSource, NativeSqlScriptExecutor nativeSqlScriptExecutor) {
        this.dataSource = dataSource;
        this.nativeSqlScriptExecutor = nativeSqlScriptExecutor;
    }

    private boolean isColumnExists(String table, String column) {
        String query = String.format(
                "SELECT * FROM INFORMATION_SCHEMA.COLUMNS\n" +
                        "WHERE TABLE_NAME = '%s'\n" +
                        "AND COLUMN_NAME = '%s'", table, column);
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement()) {
            return stmt.executeQuery(query).next();
        } catch (SQLException e) {
            return false;
        }
    }

    @PostConstruct
    public void runNativeSql() {
        if (!isColumnExists("comments", "tsv")) {
            String scriptPath = "full_text_init_postgres_no_weights.sql";
            log.info("Initialize Postgres full text search: " + scriptPath);
            nativeSqlScriptExecutor.runNativeSql(scriptPath);
        }
    }

}
