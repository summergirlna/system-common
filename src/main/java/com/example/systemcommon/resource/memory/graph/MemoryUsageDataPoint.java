package com.example.systemcommon.resource.memory.graph;

import java.time.Instant;

public record MemoryUsageDataPoint(Instant collectedAt, double usedMemoryRatePercent, double usedSwapRatePercent) {}
