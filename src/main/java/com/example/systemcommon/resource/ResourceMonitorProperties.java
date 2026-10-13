package com.example.systemcommon.resource;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.resource-monitor")
public record ResourceMonitorProperties(
        Path outputDirectory, Path pidFile, Duration interval, List<String> processKeywords, List<Path> fileStores) {}
