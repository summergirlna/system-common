package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.monitor.ResourceMonitorRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceMonitorRunJob extends AbstractOperationJob {

    private final ResourceMonitorRunner resourceMonitorRunner;

    @Override
    protected String operationName() {
        return "リソース情報定期取得";
    }

    @Override
    protected OperationResult executeOperation() {
        // 本ジョブはResourceMonitorStartJobからバックグラウンドで実行されるジョブ。
        // ユーザが直接実行するものではない。
        return resourceMonitorRunner.run();
    }

    @Override
    public String name() {
        return "resource-monitor-run";
    }
}
