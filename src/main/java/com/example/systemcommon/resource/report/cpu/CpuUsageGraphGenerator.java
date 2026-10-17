package com.example.systemcommon.resource.report.cpu;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface CpuUsageGraphGenerator {

    void generate(List<CpuUsageDataPoint> dataPoints, Path outputPath) throws IOException;
}
