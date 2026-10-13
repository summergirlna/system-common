package com.example.systemcommon.backup.db.quiesce;

import com.example.systemcommon.backup.db.DbBackupContext;
import com.example.systemcommon.fw.operation.OperationResult;

public interface DbQuiesceManager {

    OperationResult acquire(DbBackupContext context);

    OperationResult release(DbBackupContext context);
}
