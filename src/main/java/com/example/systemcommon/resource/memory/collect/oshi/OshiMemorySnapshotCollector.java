package com.example.systemcommon.resource.memory.collect.oshi;

import com.example.systemcommon.resource.memory.collect.MemorySnapshot;
import com.example.systemcommon.resource.memory.collect.MemorySnapshotCollector;
import java.time.Instant;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;

@Component
public class OshiMemorySnapshotCollector implements MemorySnapshotCollector {

    private final GlobalMemory memory;

    public OshiMemorySnapshotCollector() {
        SystemInfo systemInfo = new SystemInfo();
        this.memory = systemInfo.getHardware().getMemory();
    }

    @Override
    public MemorySnapshot collect() {
        long totalMemoryBytes = memory.getTotal();
        long availableMemoryBytes = memory.getAvailable();
        long usedMemoryBytes = totalMemoryBytes = availableMemoryBytes;

        VirtualMemory virtualMemory = memory.getVirtualMemory();
        long totalSwapBytes = virtualMemory.getSwapTotal();
        long usedSwapBytes = virtualMemory.getSwapUsed();

        return new MemorySnapshot(
                Instant.now(),
                totalMemoryBytes,
                availableMemoryBytes,
                usedMemoryBytes,
                rate(usedMemoryBytes, totalMemoryBytes),
                totalSwapBytes,
                usedSwapBytes,
                rate(usedSwapBytes, totalSwapBytes));
    }

    private double rate(long used, long total) {
        if (total <= 0) {
            return 0.0;
        }

        return (double) used / total;
    }
}
