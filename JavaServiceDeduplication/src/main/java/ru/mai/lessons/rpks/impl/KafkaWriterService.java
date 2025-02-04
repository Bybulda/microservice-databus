package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.mai.lessons.rpks.KafkaWriter;
import ru.mai.lessons.rpks.model.Message;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class KafkaWriterService implements KafkaWriter {
    private final Config config;
    private final KafkaProducer<String, String> producer;

    @Override
    public void processing(Message message) {
        if (message.isDeduplicationState()) {
            producer.send(new ProducerRecord<>(config.getString("kafka.producer.topic"), message.getValue()));
            log.info(String.format("Message: [ %s ]  Was successfully processed", message.getValue()));
        } else {
            log.info(String.format("Could not process message %s", message.getValue()));
        }

    }

    public KafkaWriterService(Config config) {
        this.config = config;
        producer = new KafkaProducer<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.producer.bootstrap.servers"),
                ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()),
                new StringSerializer(), new StringSerializer());
    }
}