package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.mai.lessons.rpks.KafkaWriter;
import ru.mai.lessons.rpks.model.ConfigNames;
import ru.mai.lessons.rpks.model.Message;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class KafkaWriterService implements KafkaWriter {
    private final KafkaProducer<String, String> producer;

    private String topic;

    public KafkaWriterService(Config config) {

        topic = config.getString(ConfigNames.KAFKA_PRODUCER_TOPIC.getProperty());
        producer = new KafkaProducer<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString(ConfigNames.KAFKA_PRODUCER_BOOTSTRAP_SERVERS.getProperty()),
                ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()),
                new StringSerializer(), new StringSerializer());
    }

    @Override
    public void processing(Message message) {
        producer.send(new ProducerRecord<>(topic, message.getValue()));
        log.debug(String.format("Message: [ %s ]  Was successfully processed", message.getValue()));

    }
}
