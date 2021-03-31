package com.ctzn.ytsservice.application.ftsinitializer;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Profile("h2db")
public class NativeSqlScriptExecutor {

    private DataSource dataSource;

    public NativeSqlScriptExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void runNativeSql(String scriptPath) {
        ClassPathResource resource = new ClassPathResource("full_text_init_h2db.sql");
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, resource);
        } catch (SQLException | ScriptException e) {
            e.printStackTrace();
        }
    }

}
