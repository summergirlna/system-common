package com.example.systemcommon.logbackup.cleanup;

import com.example.systemcommon.fw.operation.OperationResult;

public interface LogCleanupExecutor {
    OperationResult cleanup();
}
