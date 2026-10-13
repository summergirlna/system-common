package com.example.systemcommon.dbbackup;

import com.example.systemcommon.fw.operation.OperationResult;

public interface DbBackupProcessor {

    OperationResult backup(DbBackupContext context);
}
