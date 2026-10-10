package com.example.systemcommon.job.cluster;

import com.example.systemcommon.cluster.ClusterStopper;
import com.example.systemcommon.job.Job;
import com.example.systemcommon.job.JobExitCode;
import com.example.systemcommon.job.JobResult;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterStopJob implements Job {

    private final ClusterStopper clusterStopper;

    @Override
    public String name() {
        return "cluster-stop";
    }

    @Override
    public JobResult execute() {
        log.info("クラスタ停止処理を開始します。");

        OperationResult result = clusterStopper.stop();

        if (result.isFailure()) {
            log.error("クラスタ停止処理が異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("クラスタ停止処理が正常に終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
