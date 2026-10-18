package com.example.systemcommon.resource.memory.collect;

import com.example.systemcommon.resource.common.snapshot.ResourceSnapshot;
import java.time.Instant;

public record MemorySnapshot(
        Instant collectedAt,
        long totalMemoryBytes,
        long availableMemoryBytes,
        long usedMemoryBytes,
        double usedMemoryRate,
        long totalSwapBytes,
        long usedSwapBytes,
        double usedSwapRate)
        implements ResourceSnapshot {}
