package com.everything.prompt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.everything.prompt.mapper")
@EnableAsync
public class EverythingPromptApplication {
    public static void main(String[] args) {
        SpringApplication.run(EverythingPromptApplication.class, args);
    }
}
