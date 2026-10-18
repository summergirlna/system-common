package com.example.systemcommon.resource.graph.cpu;

import java.util.List;

public interface CpuUsageDataReader {
    List<CpuUsageDataPoint> read();
}
