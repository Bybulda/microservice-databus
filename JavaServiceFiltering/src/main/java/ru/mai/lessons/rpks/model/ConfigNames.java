package ru.mai.lessons.rpks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConfigNames {
    JDBC_URL("db.jdbcUrl"), DB_USER("db.user"), DB_PASSWORD("db.password"), DB_DRIVER("db.driver"),

    RULE_ID("rule_id"), FILTER_ID("filter_id"), FILTER_VALUE("filter_value"), FILTER_FUNCTION_NAME("filter_function_name"), FIELD_NAME("field_name"),

    TABLE_NAME("filter_rules"),

    KAFKA_CONSUMER_BOOTSTRAP_SERVERS("kafka.consumer.bootstrap.servers"), KAFKA_CONSUMER_GROUP_ID("kafka.consumer.group.id"),
    KAFKA_CONSUMER_AUTO_OFFSET_RESET("kafka.consumer.auto.offset.reset"), KAFKA_CONSUMER_TOPIC("kafka.consumer.topic"),

    KAFKA_PRODUCER_BOOTSTRAP_SERVERS("kafka.producer.bootstrap.servers"), KAFKA_PRODUCER_TOPIC("kafka.producer.topic"),

    UPDATE_INTERVAL("application.updateIntervalSec");

    private final String property;


}
