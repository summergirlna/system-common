package com.example.systemcommon.backup.log.cleanup.file;

import com.example.systemcommon.backup.log.AuditLogBackupProperties;
import com.example.systemcommon.backup.log.cleanup.LogCleanupExecutor;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileAuditLogCleanupExecutor implements LogCleanupExecutor {

    private final AuditLogBackupProperties properties;

    private final FileLogCleanupService cleanupService;

    @Override
    public OperationResult cleanup() {
        return cleanupService.cleanup(properties);
    }
}
