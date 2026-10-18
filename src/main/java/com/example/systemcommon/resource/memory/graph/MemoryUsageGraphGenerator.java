package com.example.systemcommon.resource.memory.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface MemoryUsageGraphGenerator {
    void generate(List<MemoryUsageDataPoint> dataPoints, Path outputPath) throws IOException;
}
