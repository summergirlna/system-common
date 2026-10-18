package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.report.cpu.CpuUsageGraphReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceGraphGenerateJob extends AbstractOperationJob {

    private final CpuUsageGraphReportService cpuUsageGraphReportService;

    @Override
    protected String operationName() {
        return "リソースグラフ生成";
    }

    @Override
    protected OperationResult executeOperation() {
        return cpuUsageGraphReportService.generate();
    }

    @Override
    public String name() {
        return "resource-graph-generate";
    }
}
