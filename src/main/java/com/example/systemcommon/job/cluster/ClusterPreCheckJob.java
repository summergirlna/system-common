package com.example.systemcommon.job.cluster;

import com.example.systemcommon.cluster.ClusterPreChecker;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.job.AbstractOperationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterPreCheckJob extends AbstractOperationJob {

    private final ClusterPreChecker clusterPreChecker;

    @Override
    public String name() {
        return "cluster-pre-check";
    }

    @Override
    protected String operationName() {
        return "クラスタ起動前チェック";
    }

    @Override
    protected OperationResult executeOperation() {
        return clusterPreChecker.check();
    }
}
