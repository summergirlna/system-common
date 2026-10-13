package com.example.systemcommon.backup.db.validate;

import com.example.systemcommon.backup.db.DbBackupContext;
import com.example.systemcommon.fw.operation.OperationResult;

public interface DbBackupValidator {

    OperationResult validate(DbBackupContext context);
}
