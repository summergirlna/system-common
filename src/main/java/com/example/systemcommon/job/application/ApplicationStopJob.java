package com.example.systemcommon.job.application;

import com.example.systemcommon.application.ApplicationStopper;
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
public class ApplicationStopJob implements Job {

    private final ApplicationStopper applicationStopper;

    @Override
    public String name() {
        return "application-stop";
    }

    @Override
    public JobResult execute() {
        log.info("アプリケーション停止ジョブを開始します。");

        OperationResult result = applicationStopper.stop();

        if (result.isFailure()) {
            log.error("アプリケーション停止ジョブが異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("アプリケーション停止ジョブが正常終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
