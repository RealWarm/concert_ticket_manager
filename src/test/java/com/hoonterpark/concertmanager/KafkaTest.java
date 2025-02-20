package com.hoonterpark.concertmanager;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KafkaTest {
    @Autowired
    private ProducerCreate producerCreate;

    @Test
    void setTestProducer() {
        producerCreate.create();
    }

}