package com.example.systemcommon.middleware;

import com.example.systemcommon.script.ScriptOperationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.middleware")
public record MiddlewareProperties(ScriptOperationProperties start, ScriptOperationProperties check) {}
