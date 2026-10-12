package com.example.systemcommon.logbackup.cleanup.file;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.cleanup.LogCleanupExecutor;
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
