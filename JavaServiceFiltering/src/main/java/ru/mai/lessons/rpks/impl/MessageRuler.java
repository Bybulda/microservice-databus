package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.RuleProcessor;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;


@Slf4j
public class MessageRuler implements RuleProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Message processing(Message message, Rule[] rules) {
        if (message.getValue() == null || message.getValue().isEmpty() || rules == null || rules.length == 0) {
            message.setFilterState(false);
            return message;
        }
        try {
            ObjectNode node = objectMapper.readValue(message.getValue(), ObjectNode.class);
            for (Rule rule : rules) {
                String filterName = rule.getFilterFunctionName();
                String fieldName = rule.getFieldName();
                String filterValue = rule.getFilterValue();
                if (node.has(fieldName)) {
                    String messageField = node.get(fieldName).asText();
                    if (!assertMessage(messageField, filterValue, filterName)) {
                        message.setFilterState(false);
                        return message;
                    }
                } else {
                    message.setFilterState(false);
                    return message;
                }

            }
            message.setFilterState(true);
        } catch (Exception e) {
            log.error("error", e);
        }
        return message;
    }

    private boolean assertMessage(String fieldName, String filterValue, String filterFunction) {
        return switch (filterFunction.toLowerCase()) {
            case "equals" -> fieldName.equals(filterValue);
            case "not_equals" -> !fieldName.equals(filterValue);
            case "contains" -> fieldName.contains(filterValue);
            case "not_contains" -> !fieldName.contains(filterValue);
            default -> false;
        };
    }
}
