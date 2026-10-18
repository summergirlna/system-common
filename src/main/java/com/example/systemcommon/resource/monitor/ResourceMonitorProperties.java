package com.example.systemcommon.resource.monitor;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.resource-monitor")
public record ResourceMonitorProperties(
        Path outputDirectory,
        Path reportDirectory,
        Path pidFile,
        Path runScript,
        Duration interval,
        Duration stopTimeout,
        List<String> processKeywords,
        List<Path> fileStores) {}
