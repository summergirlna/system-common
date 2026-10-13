package com.example.systemcommon.backup.log.backup;

import com.example.systemcommon.fw.operation.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditLogBackupOperation {
    private final LogBackupExecutor logBackupExecutor;

    public AuditLogBackupOperation(@Qualifier("fileAuditLogBackupExecutor") LogBackupExecutor logBackupExecutor) {
        this.logBackupExecutor = logBackupExecutor;
    }

    public OperationResult execute() {
        log.info("監査ログ退避ジョブを開始します。");

        OperationResult result = logBackupExecutor.backup();
        if (result.isFailure()) {
            log.error("監査ログ退避ジョブが異常終了しました。message={}", result.message());
            return result;
        }

        log.info("監査ログ退避ジョブが正常終了しました。message={}", result.message());
        return result;
    }
}
