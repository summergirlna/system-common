package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.monitor.ResourceMonitorStopper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceMonitorStopJob extends AbstractOperationJob {

    private final ResourceMonitorStopper resourceMonitorStopper;

    @Override
    protected String operationName() {
        return "リソース情報定期取得停止";
    }

    @Override
    protected OperationResult executeOperation() {
        return resourceMonitorStopper.stop();
    }

    @Override
    public String name() {
        return "resource-monitor-stop";
    }
}
