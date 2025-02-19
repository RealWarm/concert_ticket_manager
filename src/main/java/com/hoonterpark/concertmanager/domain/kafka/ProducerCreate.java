package com.hoonterpark.concertmanager.domain.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerCreate {
    private final KafkaTemplate<String, String> kafkaTemplate;


    public void create() {
        kafkaTemplate.send("topic", "hello kafka");
    }
}