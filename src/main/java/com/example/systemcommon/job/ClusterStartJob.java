package com.example.systemcommon.job;

import com.example.systemcommon.cluster.ClusterStartResult;
import com.example.systemcommon.cluster.ClusterStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterStartJob implements Job {

    private final ClusterStarter clusterStarter;

    @Override
    public String name() {
        return "cluster-start";
    }

    @Override
    public JobResult execute() {
        log.info("クラスタ起動処理を開始します。");

        ClusterStartResult result = clusterStarter.start();

        if (result.isFailure()) {
            log.error("クラスタ起動処理が異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("クラスタ起動処理が正常に終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
