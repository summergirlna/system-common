package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.command.CommandOperationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.cluster.ha-monitor")
public record HaMonitorProperties(
        CommandOperationProperties monitorPath,
        CommandOperationProperties resetPath,
        CommandOperationProperties start,
        CommandOperationProperties status,
        CommandOperationProperties stop) {}
