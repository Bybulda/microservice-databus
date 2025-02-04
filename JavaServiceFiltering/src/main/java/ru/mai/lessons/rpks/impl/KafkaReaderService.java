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
import ru.mai.lessons.rpks.model.ConfigNames;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaReaderService implements KafkaReader {
    private Map<String, String> configConsumer;
    private KafkaConsumer<String, String> kafkaConsumer;
    private KafkaWriter kafkaWriter;
    private MessageRuler messageRuler;
    private DataBaseReader dataBaseReader;
    private Rule[] rules;

    public KafkaReaderService(Config config) {
        configConsumer = fillConsumerConfig(config);
        kafkaConsumer = new KafkaConsumer<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configConsumer.get(ConfigNames.KAFKA_CONSUMER_BOOTSTRAP_SERVERS.getProperty()),
                ConsumerConfig.GROUP_ID_CONFIG, configConsumer.get(ConfigNames.KAFKA_CONSUMER_GROUP_ID.getProperty()),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, configConsumer.get(ConfigNames.KAFKA_CONSUMER_AUTO_OFFSET_RESET.getProperty())),
                new StringDeserializer(), new StringDeserializer());
        kafkaWriter = new KafkaWriterService(config);
        dataBaseReader = new DataBaseReader(config);
        messageRuler = new MessageRuler();
        rules = dataBaseReader.readRulesFromDB();
        kafkaConsumer.subscribe(Collections.singleton(configConsumer.get(ConfigNames.KAFKA_CONSUMER_TOPIC.getProperty())));

    }

    private Map<String, String> fillConsumerConfig(Config config){
        Map<String, String> map = new HashMap<>();
        map.put(ConfigNames.KAFKA_CONSUMER_BOOTSTRAP_SERVERS.getProperty(), config.getString(ConfigNames.KAFKA_CONSUMER_BOOTSTRAP_SERVERS.getProperty()));
        map.put(ConfigNames.KAFKA_CONSUMER_GROUP_ID.getProperty(), config.getString(ConfigNames.KAFKA_CONSUMER_GROUP_ID.getProperty()));
        map.put(ConfigNames.KAFKA_CONSUMER_AUTO_OFFSET_RESET.getProperty(), config.getString(ConfigNames.KAFKA_CONSUMER_AUTO_OFFSET_RESET.getProperty()));
        map.put(ConfigNames.KAFKA_CONSUMER_TOPIC.getProperty(), config.getString(ConfigNames.KAFKA_CONSUMER_TOPIC.getProperty()));
        map.put(ConfigNames.UPDATE_INTERVAL.getProperty(), config.getString(ConfigNames.UPDATE_INTERVAL.getProperty()));
        return map;
    }

    @Override
    public void processing() {

        ScheduledExecutorService ruleUpdater = Executors.newSingleThreadScheduledExecutor();
        ruleUpdater.scheduleAtFixedRate(this::updateRules,
                0, Integer.parseInt(configConsumer.get(ConfigNames.UPDATE_INTERVAL.getProperty())) * 1000L, TimeUnit.MILLISECONDS);
        boolean isRun = true;
        try {
            while (isRun) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    String recordMessage = consumerRecord.value();
                    if (recordMessage.equals("$exit")) {
                        isRun = false;
                    } else {
                        Message processedMessage = messageRuler.processing(new Message(recordMessage, false), rules);
                        if (processedMessage.isFilterState()){
                            kafkaWriter.processing(processedMessage);
                        }
                    }

                }
            }
        } catch (Exception e) {
            log.error("error", e);
        } finally {
            ruleUpdater.shutdown();
            try {
                if (!ruleUpdater.isTerminated()) {
                    ruleUpdater.shutdownNow();
                }
            } catch (Exception e) {
                log.error("error", e);
            }
        }


    }

    private void updateRules(){
        rules = dataBaseReader.readRulesFromDB();
    }
}
