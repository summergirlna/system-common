package com.example.systemcommon.fw.job;

import com.example.systemcommon.fw.operation.OperationResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractOperationJob implements Job {

    @Override
    public final JobResult execute() {
        String operationName = operationName();

        log.info("{}ジョブを開始します。", operationName);

        OperationResult result = executeOperation();

        if (result.isFailure()) {
            log.error("{}ジョブが異常終了しました。message={}", operationName, result.message());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        log.info("{}ジョブが正常終了しました。message={}", operationName, result.message());
        return JobResult.success(result.message());
    }

    protected abstract String operationName();

    protected abstract OperationResult executeOperation();
}
