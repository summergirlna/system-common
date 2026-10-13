package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.snapshot.CpuResourceSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceCpuSnapshotJob extends AbstractOperationJob {

    private final CpuResourceSnapshotService cpuResourceSnapshotService;

    @Override
    protected String operationName() {
        return "CPUリソース情報取得";
    }

    @Override
    protected OperationResult executeOperation() {
        return cpuResourceSnapshotService.snapshot();
    }

    @Override
    public String name() {
        return "resource-cpu-snapshot";
    }
}
