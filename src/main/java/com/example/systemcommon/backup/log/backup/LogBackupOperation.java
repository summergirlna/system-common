package com.example.systemcommon.backup.log.backup;

import com.example.systemcommon.fw.operation.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogBackupOperation {

    private final LogBackupExecutor logBackupExecutor;

    public LogBackupOperation(@Qualifier("fileLogBackupExecutor") LogBackupExecutor logBackupExecutor) {
        this.logBackupExecutor = logBackupExecutor;
    }

    public OperationResult execute() {
        log.info("ログ退避ジョブを開始します。");

        OperationResult result = logBackupExecutor.backup();
        if (result.isFailure()) {
            log.error("ログ退避ジョブが異常終了しました。message={}", result.message());
            return result;
        }

        log.info("ログ退避ジョブが正常終了しました。message={}", result.message());
        return result;
    }
}
