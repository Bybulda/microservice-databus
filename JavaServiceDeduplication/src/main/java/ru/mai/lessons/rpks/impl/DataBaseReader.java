package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.mai.lessons.rpks.DbReader;
import ru.mai.lessons.rpks.model.Rule;

import java.sql.SQLException;

@Slf4j
public class DataBaseReader implements DbReader {
    private final HikariDataSource ds;

    @Override
    public Rule[] readRulesFromDB() {
        try {
            DSLContext dslContext = DSL.using(ds.getConnection(), SQLDialect.POSTGRES);
            var rulesContext = dslContext.select().from("deduplication_rules").fetch();
            Rule[] rules = new Rule[rulesContext.size()];
            for (int i = 0; i < rules.length; i++) {
                rules[i] = Rule.builder()
                        .deduplicationId(rulesContext.get(i).getValue("deduplication_id", Long.class))
                        .ruleId(rulesContext.get(i).getValue("rule_id", Long.class))
                        .fieldName(rulesContext.get(i).getValue("field_name", String.class))
                        .timeToLiveSec(rulesContext.get(i).getValue("time_to_live_sec", Long.class))
                        .isActive(rulesContext.get(i).getValue("is_active", Boolean.class))
                        .build();
            }
            return rules;
        } catch (SQLException e) {
            log.error(String.format("Rule read went wrong, exception: [ %s ]", e.getMessage()));
        }
        return new Rule[0];
    }

    private HikariDataSource setHikariDataSource(Config config) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(config.getString("db.jdbcUrl"));
        hikariConfig.setUsername(config.getString("db.user"));
        hikariConfig.setPassword(config.getString("db.password"));
        hikariConfig.setDriverClassName(config.getString("db.driver"));
        return new HikariDataSource(hikariConfig);
    }

    public DataBaseReader(Config config) {
        ds = setHikariDataSource(config);
    }
}