package com.hoonterpark.concertmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
//@ServletComponentScan
@SpringBootApplication
public class ConcertmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcertmanagerApplication.class, args);
	}

}
