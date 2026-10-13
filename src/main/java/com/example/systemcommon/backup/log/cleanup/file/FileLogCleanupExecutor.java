package com.example.systemcommon.backup.log.cleanup.file;

import com.example.systemcommon.backup.log.LogBackupProperties;
import com.example.systemcommon.backup.log.cleanup.LogCleanupExecutor;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileLogCleanupExecutor implements LogCleanupExecutor {

    private final LogBackupProperties properties;

    private final FileLogCleanupService cleanupService;

    @Override
    public OperationResult cleanup() {
        return cleanupService.cleanup(properties);
    }
}
