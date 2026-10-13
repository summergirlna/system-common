package com.example.systemcommon.backup.db.backup;

import com.example.systemcommon.backup.db.DbBackupContext;
import com.example.systemcommon.fw.operation.OperationResult;

public interface DbBackupProcessor {

    OperationResult backup(DbBackupContext context);
}
