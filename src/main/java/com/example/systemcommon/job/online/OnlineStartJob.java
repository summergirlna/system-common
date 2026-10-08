package com.example.systemcommon.job.online;

import com.example.systemcommon.job.Job;
import com.example.systemcommon.job.JobExitCode;
import com.example.systemcommon.job.JobResult;
import com.example.systemcommon.online.OnlineStarter;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnlineStartJob implements Job {

    private final OnlineStarter onlineStarter;

    @Override
    public String name() {
        return "online-start";
    }

    @Override
    public JobResult execute() {
        log.info("オンライン業務開始ジョブを開始します。");

        OperationResult result = onlineStarter.start();

        if (result.isFailure()) {
            log.error("オンライン業務開始ジョブが異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("オンライン業務開始ジョブが正常終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
