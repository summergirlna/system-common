package com.example.systemcommon.backup.db;

import com.example.systemcommon.fw.operation.OperationResult;

public record BackupExecutionResult(OperationResult backupResult, OperationResult validateResult) {}
