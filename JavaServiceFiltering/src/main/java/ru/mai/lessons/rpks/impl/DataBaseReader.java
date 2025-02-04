package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.mai.lessons.rpks.DbReader;
import ru.mai.lessons.rpks.model.ConfigNames;
import ru.mai.lessons.rpks.model.Rule;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DataBaseReader implements DbReader {
    private final HikariDataSource ds;
    private Map<String, String> configHikari;

    public DataBaseReader(Config config) {
        ds = createHikariDataSource(config);
    }

    @Override
    public Rule[] readRulesFromDB() {
        try {
            DSLContext dslContext = DSL.using(ds.getConnection(), SQLDialect.POSTGRES);
            var rulesContext = dslContext.select().from(ConfigNames.TABLE_NAME.getProperty()).fetch();
            Rule[] rules = new Rule[rulesContext.size()];
            for (int i = 0; i < rules.length; i++) {
                rules[i] = Rule.builder()
                        .ruleId(rulesContext.get(i).getValue(ConfigNames.RULE_ID.getProperty(), Long.class))
                        .filterId(rulesContext.get(i).getValue(ConfigNames.FILTER_ID.getProperty(), Long.class))
                        .filterValue(rulesContext.get(i).getValue(ConfigNames.FILTER_VALUE.getProperty(), String.class))
                        .filterFunctionName(rulesContext.get(i).getValue(ConfigNames.FILTER_FUNCTION_NAME.getProperty(), String.class))
                        .fieldName(rulesContext.get(i).getValue(ConfigNames.FIELD_NAME.getProperty(), String.class))
                        .build();
            }
            return rules;
        } catch (SQLException e) {
            log.error(String.format("Rule read gone wrong, exception: %s", e.getMessage()));
        }
        return new Rule[0];
    }

    private void fillConfigMap(Config configFile){
        configHikari = new HashMap<>();
        configHikari.put(ConfigNames.DB_DRIVER.getProperty(), configFile.getString(ConfigNames.DB_DRIVER.getProperty()));
        configHikari.put(ConfigNames.DB_USER.getProperty(), configFile.getString(ConfigNames.DB_USER.getProperty()));
        configHikari.put(ConfigNames.DB_PASSWORD.getProperty(), configFile.getString(ConfigNames.DB_PASSWORD.getProperty()));
        configHikari.put(ConfigNames.JDBC_URL.getProperty(), configFile.getString(ConfigNames.JDBC_URL.getProperty()));
    }

    private HikariDataSource createHikariDataSource(Config config) {
        HikariConfig hikariConfig = new HikariConfig();
        fillConfigMap(config);

        hikariConfig.setJdbcUrl(configHikari.get(ConfigNames.JDBC_URL.getProperty()));
        hikariConfig.setUsername(configHikari.get(ConfigNames.DB_USER.getProperty()));
        hikariConfig.setPassword(configHikari.get(ConfigNames.DB_PASSWORD.getProperty()));
        hikariConfig.setDriverClassName(configHikari.get(ConfigNames.DB_DRIVER.getProperty()));
        return new HikariDataSource(hikariConfig);
    }
}
