package com.example.systemcommon.job;

import com.example.systemcommon.cluster.ClusterChecker;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterCheckJob implements Job {

    private final ClusterChecker clusterChecker;

    @Override
    public String name() {
        return "cluster-check";
    }

    @Override
    public JobResult execute() {
        log.info("クラスタ起動確認処理を開始します。");

        OperationResult result = clusterChecker.check();

        if (result.isFailure()) {
            log.error("クラスタ起動確認処理が異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("クラスタ起動確認処理が正常に終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
