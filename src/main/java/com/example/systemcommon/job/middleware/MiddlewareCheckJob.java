package com.example.systemcommon.job.middleware;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.job.Job;
import com.example.systemcommon.job.JobExitCode;
import com.example.systemcommon.job.JobResult;
import com.example.systemcommon.middleware.MiddlewareChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MiddlewareCheckJob implements Job {

    private final MiddlewareChecker middlewareChecker;

    @Override
    public String name() {
        return "middleware-check";
    }

    @Override
    public JobResult execute() {
        log.info("ミドルウェアチェックジョブを開始します。");

        OperationResult result = middlewareChecker.check();

        if (result.isFailure()) {
            log.error("ミドルウェアチェックジョブが異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("ミドルウェアチェックジョブが正常終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
