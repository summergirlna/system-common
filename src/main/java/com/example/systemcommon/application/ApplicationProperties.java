package com.example.systemcommon.application;

import com.example.systemcommon.fw.script.ScriptOperationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.application")
public record ApplicationProperties(ScriptOperationProperties start, ScriptOperationProperties stop) {}
