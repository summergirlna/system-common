package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.monitor.ResourceMonitorStarter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceMonitorStartJob extends AbstractOperationJob {

    private final ResourceMonitorStarter resourceMonitorStarter;

    @Override
    protected String operationName() {
        return "リソース情報定期取得開始";
    }

    @Override
    protected OperationResult executeOperation() {
        return resourceMonitorStarter.start();
    }

    @Override
    public String name() {
        return "resource-monitor-start";
    }
}
