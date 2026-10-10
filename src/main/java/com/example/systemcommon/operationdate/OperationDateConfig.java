package com.example.systemcommon.operationdate;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OperationDateConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
