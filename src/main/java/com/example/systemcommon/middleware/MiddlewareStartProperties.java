package com.example.systemcommon.middleware;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.middleware.start")
public record MiddlewareStartProperties(List<MiddlewareProductProperties> products) {}
