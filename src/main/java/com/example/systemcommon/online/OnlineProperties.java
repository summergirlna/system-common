package com.example.systemcommon.online;

import com.example.systemcommon.script.ScriptOperationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.online")
public record OnlineProperties(ScriptOperationProperties start, ScriptOperationProperties stop) {}
