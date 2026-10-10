package com.example.systemcommon.job;

import com.example.systemcommon.cluster.ClusterChecker;
import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterCheckJob extends AbstractOperationJob {

    private final ClusterChecker clusterChecker;

    @Override
    public String name() {
        return "cluster-check";
    }

    @Override
    protected String operationName() {
        return "クラスタ起動確認";
    }

    @Override
    protected OperationResult executeOperation() {
        return clusterChecker.check();
    }
}
