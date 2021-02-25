package com.ctzn.ytsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YtsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtsServiceApplication.class, args);
    }

//    @Autowired
//    CommentRepository commentRepository;
//
//    @Bean
//    CommandLineRunner runner() {
//        return args -> {
//            commentRepository.findAll(PageRequest.of(0, 100)).forEach(System.out::println);
//        };
//    }

}
