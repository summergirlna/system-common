package com.example.systemcommon.backup.db.validate;

import com.example.systemcommon.backup.db.DbBackupContext;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoopDbBackupValidator implements DbBackupValidator {
    @Override
    public OperationResult validate(DbBackupContext context) {
        log.info("DBバックアップ検証はスキップします。backupId={}", context.backupId());
        return OperationResult.success("DBバックアップ検証はスキップしました。");
    }
}
