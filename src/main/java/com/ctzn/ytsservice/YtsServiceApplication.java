package com.ctzn.ytsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class YtsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtsServiceApplication.class, args);
    }

}
