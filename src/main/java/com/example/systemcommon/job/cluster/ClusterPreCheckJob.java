package com.example.systemcommon.job.cluster;

import com.example.systemcommon.cluster.ClusterPreChecker;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.job.Job;
import com.example.systemcommon.job.JobExitCode;
import com.example.systemcommon.job.JobResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterPreCheckJob implements Job {

    private final ClusterPreChecker clusterPreChecker;

    @Override
    public String name() {
        return "cluster-pre-check";
    }

    @Override
    public JobResult execute() {
        log.info("クラスタ起動前チェック処理を開始します。");

        OperationResult result = clusterPreChecker.check();

        if (result.isFailure()) {
            log.error("クラスタ起動前チェック処理が異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("クラスタ起動前チェック処理が正常に終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
