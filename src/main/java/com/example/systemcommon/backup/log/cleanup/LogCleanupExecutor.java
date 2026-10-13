package com.example.systemcommon.backup.log.cleanup;

import com.example.systemcommon.fw.operation.OperationResult;

public interface LogCleanupExecutor {
    OperationResult cleanup();
}
