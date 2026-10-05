package com.example.systemcommon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SystemCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemCommonApplication.class, args);
    }
}
