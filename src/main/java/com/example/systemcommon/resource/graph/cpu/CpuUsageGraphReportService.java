package com.example.systemcommon.resource.graph.cpu;

import com.example.systemcommon.fw.operation.OperationResult;
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
public class CpuUsageGraphReportService {

    private static final String OUTPUT_FILE_NAME = "cpu-usage.png";

    private final ResourceMonitorProperties properties;

    private final CpuUsageDataReader cpuUsageDataReader;

    private final CpuUsageGraphGenerator cpuUsageGraphGenerator;

    public OperationResult generate() {
        log.info("CPU使用率グラフの生成を開始します。");

        List<CpuUsageDataPoint> dataPoints = cpuUsageDataReader.read();
        if (dataPoints.isEmpty()) {
            log.error("CPU使用率グラフ生成対象データが存在しません。outputDirectory={}", properties.outputDirectory());
            return OperationResult.failure("CPU使用率グラフ生成対象データが存在しません。");
        }

        Path outputPath = properties.reportDirectory().resolve(OUTPUT_FILE_NAME);

        try {
            cpuUsageGraphGenerator.generate(dataPoints, outputPath);
            log.info("CPU使用率グラフを生成しました。outputPath={}", outputPath);
            return OperationResult.success("CPU使用率グラフを生成しました: " + outputPath);
        } catch (IOException e) {
            log.error("CPU使用率グラフの生成に失敗しました。outputPath={}", outputPath, e);
            return OperationResult.failure("CPU使用率グラフの生成に失敗しました: " + outputPath);
        }
    }
}
