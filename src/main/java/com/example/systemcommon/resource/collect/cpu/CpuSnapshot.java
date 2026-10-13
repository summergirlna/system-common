package com.example.systemcommon.resource.collect.cpu;

import java.time.Instant;

public record CpuSnapshot(
        Instant collectedAt,
        int logicalProcessorCount,
        int physicalProcessorCount,
        double systemCpuLoad,
        double userRate,
        double systemRate,
        double idleRate,
        double ioWaitRate,
        double systemLoadAverage1m,
        double systemLoadAverage5m,
        double systemLoadAverage15m) {}
