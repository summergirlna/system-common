package com.example.systemcommon.online;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.online")
public record OnlineProperties(OnlineOperationProperties start) {}
