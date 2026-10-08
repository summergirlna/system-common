package com.example.systemcommon.middleware;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.middleware")
public record MiddlewareProperties(MiddlewareOperationProperties start, MiddlewareOperationProperties check) {}
