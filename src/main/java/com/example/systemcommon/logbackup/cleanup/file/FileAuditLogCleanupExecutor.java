package com.example.systemcommon.logbackup.cleanup.file;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.AuditLogBackupProperties;
import com.example.systemcommon.logbackup.cleanup.LogCleanupExecutor;
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
