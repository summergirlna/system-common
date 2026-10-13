package com.example.systemcommon.dbbackup;

import com.example.systemcommon.fw.operation.OperationResult;

public interface DbBackupValidator {

    OperationResult validate(DbBackupContext context);
}
