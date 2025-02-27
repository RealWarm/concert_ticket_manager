package com.hoonterpark.concertmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableTransactionManagement
public class ConcertmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcertmanagerApplication.class, args);
	}

}
