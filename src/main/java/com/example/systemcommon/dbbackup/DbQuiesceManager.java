package com.example.systemcommon.dbbackup;

import com.example.systemcommon.fw.operation.OperationResult;

public interface DbQuiesceManager {

    OperationResult acquire(DbBackupContext context);

    OperationResult release(DbBackupContext context);
}
