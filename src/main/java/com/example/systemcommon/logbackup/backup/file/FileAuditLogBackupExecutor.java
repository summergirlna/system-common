package com.example.systemcommon.logbackup.backup.file;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.AuditLogBackupProperties;
import com.example.systemcommon.logbackup.backup.LogBackupExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileAuditLogBackupExecutor implements LogBackupExecutor {

    private final AuditLogBackupProperties properties;

    private final FileLogBackupService backupService;

    @Override
    public OperationResult backup() {
        return backupService.backup(properties);
    }
}
