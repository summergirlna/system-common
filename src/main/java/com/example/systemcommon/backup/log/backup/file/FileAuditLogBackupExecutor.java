package com.example.systemcommon.backup.log.backup.file;

import com.example.systemcommon.backup.log.AuditLogBackupProperties;
import com.example.systemcommon.backup.log.backup.LogBackupExecutor;
import com.example.systemcommon.fw.operation.OperationResult;
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
