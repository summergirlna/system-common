package com.example.systemcommon.logbackup.backup.file;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.backup.LogBackupExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileLogBackupExecutor implements LogBackupExecutor {

    private final LogBackupProperties properties;

    private final FileLogBackupService backupService;

    @Override
    public OperationResult backup() {
        return backupService.backup(properties);
    }
}
