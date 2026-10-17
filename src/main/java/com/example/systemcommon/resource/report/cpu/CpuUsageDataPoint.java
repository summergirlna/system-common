package com.example.systemcommon.resource.report.cpu;

import java.time.Instant;

public record CpuUsageDataPoint(
        Instant collectedAt,
        double systemCpuLoadPercent,
        double userRatePercent,
        double systemRatePercent,
        double idleRatePercent,
        double ioWaitRatePercent) {}
