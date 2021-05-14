package com.ctzn.ytsservice.application.ftsinitializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@Profile("h2db")
@Slf4j
public class FtsInitializerH2DB implements FtsInitializer {

    private DataSource dataSource;
    private NativeSqlScriptExecutor nativeSqlScriptExecutor;

    public FtsInitializerH2DB(DataSource dataSource, NativeSqlScriptExecutor nativeSqlScriptExecutor) {
        this.dataSource = dataSource;
        this.nativeSqlScriptExecutor = nativeSqlScriptExecutor;
    }

    private boolean isTableIndexed(String table) {
        String query = String.format("SELECT * FROM FT.INDEXES where `TABLE`='%s'", table);
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement()) {
            return stmt.executeQuery(query).next();
        } catch (SQLException e) {
            return false;
        }
    }

    @PostConstruct
    public void runNativeSql() {
        if (!isTableIndexed("COMMENTS")) {
            String scriptPath = "full_text_init_h2db.sql";
            log.info("Initialize H2DB full text search: [scriptPath: {}]", scriptPath);
            nativeSqlScriptExecutor.runNativeSql(scriptPath);
        }
    }

}
