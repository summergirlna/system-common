package com.example.systemcommon.resource.memory.graph;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.common.graph.ResourceGraphReportGenerator;
import com.example.systemcommon.resource.monitor.ResourceMonitorProperties;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemoryUsageGraphReportService implements ResourceGraphReportGenerator {

    private static final String OUTPUT_FILE_NAME = "memory-usage.png";

    private final ResourceMonitorProperties properties;

    private final MemoryUsageDataReader memoryUsageDataReader;

    private final MemoryUsageGraphGenerator memoryUsageGraphGenerator;

    @Override
    public OperationResult generate() {
        log.info("メモリ使用率グラフの生成を開始します。");

        List<MemoryUsageDataPoint> dataPoints = memoryUsageDataReader.read();
        if (dataPoints.isEmpty()) {
            log.error("メモリ使用率グラフ生成対象データが存在しません。outputDirectory={}", properties.outputDirectory());
            return OperationResult.failure("メモリ使用率グラフ生成対象データが存在しません。");
        }

        Path outputPath = properties.reportDirectory().resolve(OUTPUT_FILE_NAME);

        try {
            memoryUsageGraphGenerator.generate(dataPoints, outputPath);
            log.info("メモリ使用率グラフを生成しました。outputPath={}", outputPath);
            return OperationResult.success("メモリ使用率グラフを生成しました: " + outputPath);
        } catch (IOException e) {
            log.error("メモリ使用率グラフの生成に失敗しました。outputPath={}", outputPath, e);
            return OperationResult.failure("メモリ使用率グラフの生成に失敗しました: " + outputPath);
        }
    }
}
