package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.RuleProcessor;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;

@Slf4j
public class MessageDeduplicationService implements RuleProcessor {

    private final RedisClientService redisClientService;

    public MessageDeduplicationService(Config config){
        redisClientService = new RedisClientService(config);
    }

    @Override
    public Message processing(Message message, Rule[] rules) {
        if (message.getValue() == null || message.getValue().isEmpty()) {
            message.setDeduplicationState(false);
            return message;
        }
        if (rules == null || rules.length == 0){
            message.setDeduplicationState(true);
            return message;
        }
        message.setDeduplicationState(redisClientService.filterMessage(message, rules));
        return message;
    }
}