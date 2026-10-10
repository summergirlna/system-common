package com.example.systemcommon.job.middleware;

import com.example.systemcommon.job.Job;
import com.example.systemcommon.job.JobExitCode;
import com.example.systemcommon.job.JobResult;
import com.example.systemcommon.middleware.MiddlewareStopper;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MiddlewareStopJob implements Job {

    private final MiddlewareStopper middlewareStopper;

    @Override
    public String name() {
        return "middleware-stop";
    }

    @Override
    public JobResult execute() {
        log.info("ミドルウェア停止ジョブを開始します。");

        OperationResult result = middlewareStopper.stop();

        if (result.isFailure()) {
            log.error("ミドルウェア停止ジョブが異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("ミドルウェア停止ジョブが正常終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
