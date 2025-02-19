package com.hoonterpark.concertmanager.domain.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
//public class ConsumerListener {
//    @KafkaListener(topics = "topic", groupId = "group_1")
//    public void listener(Object data) {
//        System.out.println("!!!!!!!!!Consumed message : " + data.toString());
//    }
//}