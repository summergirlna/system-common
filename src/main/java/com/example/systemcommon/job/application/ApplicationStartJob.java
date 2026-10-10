package com.example.systemcommon.job.application;

import com.example.systemcommon.application.ApplicationStarter;
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
public class ApplicationStartJob implements Job {

    private final ApplicationStarter applicationStarter;

    @Override
    public String name() {
        return "application-start";
    }

    @Override
    public JobResult execute() {
        log.info("アプリケーション起動ジョブを開始します。");

        OperationResult result = applicationStarter.start();

        if (result.isFailure()) {
            log.error("アプリケーション起動ジョブが異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("アプリケーション起動ジョブが正常終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
