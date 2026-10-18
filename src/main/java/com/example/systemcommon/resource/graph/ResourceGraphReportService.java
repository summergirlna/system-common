package com.example.systemcommon.resource.graph;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.cpu.graph.CpuUsageGraphReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceGraphReportService {

    private final CpuUsageGraphReportService cpuUsageGraphReportService;

    public OperationResult generate() {
        log.info("リソースグラフ生成を開始します。");

        OperationResult result = cpuUsageGraphReportService.generate();
        if (result.isFailure()) {
            log.error("リソースグラフ生成が異常終了しました。message={}", result.message());
            return result;
        }

        log.info("リソースグラフ生成が正常終了しました。message={}", result.message());
        return OperationResult.success("リソースグラフ生成が正常に終了しました。");
    }
}
