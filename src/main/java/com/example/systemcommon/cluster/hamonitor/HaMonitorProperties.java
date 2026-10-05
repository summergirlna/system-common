package com.example.systemcommon.cluster.hamonitor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.cluster.ha-monitor")
public record HaMonitorProperties(
        HaMonitorCommandProperties monitorPath,
        HaMonitorCommandProperties resetPath,
        HaMonitorCommandProperties start) {}
