package com.example.systemcommon.job;

import com.example.systemcommon.backup.log.compress.LogCompressExecutor;
import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AuditLogCompressJob extends AbstractOperationJob {

    private final LogCompressExecutor logCompressExecutor;

    public AuditLogCompressJob(@Qualifier("tarGzAuditLogCompressExecutor") LogCompressExecutor logCompressExecutor) {
        this.logCompressExecutor = logCompressExecutor;
    }

    @Override
    protected String operationName() {
        return "監査ログ圧縮";
    }

    @Override
    protected OperationResult executeOperation() {
        return logCompressExecutor.compress();
    }

    @Override
    public String name() {
        return "audit-log-compress";
    }
}
