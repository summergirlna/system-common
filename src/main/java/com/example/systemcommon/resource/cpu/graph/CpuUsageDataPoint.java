package com.example.systemcommon.resource.cpu.graph;

import java.time.Instant;

public record CpuUsageDataPoint(
        Instant collectedAt,
        double systemCpuLoadPercent,
        double userRatePercent,
        double systemRatePercent,
        double idleRatePercent,
        double ioWaitRatePercent) {}
