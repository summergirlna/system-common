package com.example.systemcommon.logbackup.backup;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.logoutput.LogOutputSwitcher;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogBackupOperation {

    private final LogBackupExecutor logBackupExecutor;

    private final LogBackupProperties properties;

    private final LogOutputSwitcher logOutputSwitcher;

    public LogBackupOperation(
            @Qualifier("fileLogBackupExecutor") LogBackupExecutor logBackupExecutor,
            LogBackupProperties properties,
            LogOutputSwitcher logOutputSwitcher) {
        this.logBackupExecutor = logBackupExecutor;
        this.properties = properties;
        this.logOutputSwitcher = logOutputSwitcher;
    }

    public OperationResult execute() {
        return logOutputSwitcher.executeWithTemporaryOutput(sourcePaths(), this::executeBackup);
    }

    private List<Path> sourcePaths() {
        return properties.targets().stream()
                .map(LogBackupTargetProperties::sourcePath)
                .toList();
    }

    private OperationResult executeBackup() {
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
