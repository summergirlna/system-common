package com.example.systemcommon.resource.cpu.graph;

import java.util.List;

public interface CpuUsageDataReader {
    List<CpuUsageDataPoint> read();
}
