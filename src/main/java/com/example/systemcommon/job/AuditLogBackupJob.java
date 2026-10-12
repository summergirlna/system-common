package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.backup.AuditLogBackupOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogBackupJob extends AbstractOperationJob {

    private final AuditLogBackupOperation auditLogBackupOperation;

    @Override
    protected String operationName() {
        return "監査ログ退避";
    }

    @Override
    protected OperationResult executeOperation() {
        return auditLogBackupOperation.execute();
    }

    @Override
    public String name() {
        return "audit-log-backup";
    }
}
