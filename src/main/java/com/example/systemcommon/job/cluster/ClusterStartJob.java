package com.example.systemcommon.job.cluster;

import com.example.systemcommon.cluster.ClusterStarter;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.job.AbstractOperationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterStartJob extends AbstractOperationJob {

    private final ClusterStarter clusterStarter;

    @Override
    public String name() {
        return "cluster-start";
    }

    @Override
    protected String operationName() {
        return "クラスタ起動";
    }

    @Override
    protected OperationResult executeOperation() {
        return clusterStarter.start();
    }
}
