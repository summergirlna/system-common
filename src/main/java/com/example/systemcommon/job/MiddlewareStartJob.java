package com.example.systemcommon.job;

import com.example.systemcommon.middleware.MiddlewareStarter;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MiddlewareStartJob implements Job {

    private final MiddlewareStarter middlewareStarter;

    @Override
    public String name() {
        return "middleware-start";
    }

    @Override
    public JobResult execute() {
        log.info("ミドルウェア起動ジョブを開始します。");

        OperationResult result = middlewareStarter.start();

        if (result.isFailure()) {
            log.error("ミドルウェア起動ジョブが異常終了しました。message={}", result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("ミドルウェア起動ジョブが正常終了しました。message={}", result.message());
        return JobResult.success(result.message());
    }
}
