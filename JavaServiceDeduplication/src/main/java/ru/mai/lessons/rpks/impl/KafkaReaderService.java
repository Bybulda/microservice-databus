package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import ru.mai.lessons.rpks.KafkaReader;
import ru.mai.lessons.rpks.KafkaWriter;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaReaderService implements KafkaReader {
    private final Config config;

    private final KafkaConsumer<String, String> kafkaConsumer;
    private final KafkaWriter kafkaWriter;
    private final MessageDeduplicationService messageDeduplicationService;
    private final DataBaseReader dataBaseReader;
    private Rule[] rules;

    @Override
    public void processing() {

        ScheduledExecutorService ruleUpdater = Executors.newSingleThreadScheduledExecutor();
        ruleUpdater.scheduleAtFixedRate(this::updateRules,
                0, config.getLong("application.updateIntervalSec") * 1000, TimeUnit.MILLISECONDS);
        boolean isRun = true;
        try {
            while (isRun) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    String recordMessage = consumerRecord.value();
                    if (recordMessage.equals("$exit")) {
                        isRun = false;
                    } else {
                        Message processedMessage = messageDeduplicationService.processing(Message.builder()
                                .value(recordMessage)
                                .deduplicationState(false).build(), rules);
                        kafkaWriter.processing(processedMessage);
                    }

                }
            }
        } catch (Exception e) {
            log.error(String.format("Got an error at class: [ %s ]", this.getClass().getName()));
            log.error(e.getMessage());
        } finally {
            ruleUpdater.shutdown();
            try {
                if (!ruleUpdater.isTerminated()) {
                    ruleUpdater.shutdownNow();
                }
            } catch (Exception e) {
                log.error(String.format("Thread termination exception class: [ %s ]", this.getClass().getName()));
                log.error(e.getMessage());
            }
        }


    }

    public KafkaReaderService(Config config) {
        this.config = config;
        kafkaConsumer = new KafkaConsumer<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.consumer.bootstrap.servers"),
                ConsumerConfig.GROUP_ID_CONFIG, config.getString("kafka.consumer.group.id"),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.getString("kafka.consumer.auto.offset.reset")),
                new StringDeserializer(), new StringDeserializer());
        kafkaWriter = new KafkaWriterService(config);
        dataBaseReader = new DataBaseReader(config);
        messageDeduplicationService = new MessageDeduplicationService(config);
        rules = dataBaseReader.readRulesFromDB();
        kafkaConsumer.subscribe(Collections.singleton(config.getString("kafka.consumer.topic")));

    }

    private void updateRules(){
        rules = dataBaseReader.readRulesFromDB();
    }
}