package com.example.systemcommon.backup.log.backup.file;

import com.example.systemcommon.backup.log.LogBackupProperties;
import com.example.systemcommon.backup.log.backup.LogBackupExecutor;
import com.example.systemcommon.fw.operation.OperationResult;
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
