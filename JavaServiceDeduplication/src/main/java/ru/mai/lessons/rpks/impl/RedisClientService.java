package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPooled;
import ru.mai.lessons.rpks.RedisClient;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.MessageStateExpire;
import ru.mai.lessons.rpks.model.Rule;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RedisClientService implements RedisClient {

    private final JedisPooled jedisChannel;

    public RedisClientService(Config config){
        jedisChannel = new JedisPooled(config.getString("redis.host"), config.getInt("redis.port"));
    }

    public boolean filterMessage(Message message, Rule[] rules){
        MessageStateExpire messageStateExpire = genKey(message, rules);
        if (messageStateExpire == null) {
            log.error("Something went wrong during keyGen operation");
            return false;
        }
        String key = messageStateExpire.getMessageKey();
        if (jedisChannel.exists(key)){
            return false;
        }
        if (key != null && !key.isEmpty()){
            jedisChannel.set(key, message.getValue());
            jedisChannel.expire(key, messageStateExpire.getExpireTime());
        }
        return true;
    }

    private MessageStateExpire genKey(Message message, Rule[] rules){
        List<String> keyBuilder = new ArrayList<>();
        MessageStateExpire stateExpire = new MessageStateExpire();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(message.getValue(), ObjectNode.class);
            for(Rule rule: rules){
                if (Boolean.TRUE.equals(rule.getIsActive()) && (node.has(rule.getFieldName()))){
                        keyBuilder.add(node.get(rule.getFieldName()).asText());
                        if(stateExpire.getExpireTime() < rule.getTimeToLiveSec()){
                            stateExpire.setExpireTime(rule.getTimeToLiveSec());
                        }

                }
            }
            stateExpire.setMessageKey(String.join(":", keyBuilder));
            return stateExpire;
        } catch (Exception e) {
            log.error(String.format("Json exception at class [ %s ], error string [ %s ]", this.getClass().getName(), e.getMessage()));
        }
        return null;
    }
}
