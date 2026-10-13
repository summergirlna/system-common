package com.example.systemcommon.backup.db.quiesce.noop;

import com.example.systemcommon.backup.db.DbBackupContext;
import com.example.systemcommon.backup.db.quiesce.DbQuiesceManager;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoopDbQuiesceManager implements DbQuiesceManager {

    @Override
    public OperationResult acquire(DbBackupContext context) {
        log.info("DB静止点取得はスキップします。backupId={}", context.backupId());
        return OperationResult.success("DB静止点取得はスキップしました。");
    }

    @Override
    public OperationResult release(DbBackupContext context) {
        log.info("DB静止点解除はスキップします。backupId={}", context.backupId());
        return OperationResult.success("DB静止点解除はスキップしました。");
    }
}
