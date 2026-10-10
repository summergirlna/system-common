package com.example.systemcommon.job.cluster;

import com.example.systemcommon.cluster.ClusterStopper;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.job.AbstractOperationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterStopJob extends AbstractOperationJob {

    private final ClusterStopper clusterStopper;

    @Override
    public String name() {
        return "cluster-stop";
    }

    @Override
    protected String operationName() {
        return "クラスタ停止";
    }

    @Override
    protected OperationResult executeOperation() {
        return clusterStopper.stop();
    }
}
