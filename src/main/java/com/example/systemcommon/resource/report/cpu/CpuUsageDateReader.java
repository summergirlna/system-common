package com.example.systemcommon.resource.report.cpu;

import java.util.List;

public interface CpuUsageDateReader {
    List<CpuUsageDataPoint> read();
}
