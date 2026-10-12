package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.cleanup.LogCleanupExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LogCleanupJob extends AbstractOperationJob {

    private final LogCleanupExecutor logCleanupExecutor;

    public LogCleanupJob(@Qualifier("fileLogCleanupExecutor") LogCleanupExecutor logCleanupExecutor) {
        this.logCleanupExecutor = logCleanupExecutor;
    }

    @Override
    protected String operationName() {
        return "ログ削除";
    }

    @Override
    protected OperationResult executeOperation() {
        return logCleanupExecutor.cleanup();
    }

    @Override
    public String name() {
        return "log-cleanup";
    }
}
