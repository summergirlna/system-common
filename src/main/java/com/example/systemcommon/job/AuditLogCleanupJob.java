package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.cleanup.LogCleanupExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AuditLogCleanupJob extends AbstractOperationJob {

    private final LogCleanupExecutor logCleanupExecutor;

    public AuditLogCleanupJob(@Qualifier("fileAuditLogCleanupExecutor") LogCleanupExecutor logCleanupExecutor) {
        this.logCleanupExecutor = logCleanupExecutor;
    }

    @Override
    protected String operationName() {
        return "監査ログ削除";
    }

    @Override
    protected OperationResult executeOperation() {
        return logCleanupExecutor.cleanup();
    }

    @Override
    public String name() {
        return "audit-log-cleanup";
    }
}
