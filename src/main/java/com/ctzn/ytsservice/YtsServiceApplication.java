package com.ctzn.ytsservice;

import com.ctzn.ytsservice.domain.ChannelRunnerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
public class YtsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtsServiceApplication.class, args);
    }

    @Autowired
    ChannelRunnerFactory commentRunnerFactory;

    @Async
    void run() {
        try {
            commentRunnerFactory.newRunner("UCksTNgiRyQGwi2ODBie8HdA").call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    CommandLineRunner startWorker() {
        return args -> {
            System.out.println("before");
            run();
            System.out.println("after");
        };
    }

}
