package com.example.systemcommon.logbackup.backup;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.AuditLogBackupProperties;
import com.example.systemcommon.logbackup.logoutput.LogOutputSwitcher;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogBackupOperation {
    @Qualifier("fileAuditLogBackupExecutor") private final LogBackupExecutor logBackupExecutor;

    private final AuditLogBackupProperties properties;

    private final LogOutputSwitcher logOutputSwitcher;

    public OperationResult execute() {
        if (requiresLogOutputSwitch()) {
            return logOutputSwitcher.executeWithTemporaryOutput(this::executeBackup);
        }

        return executeBackup();
    }

    private boolean requiresLogOutputSwitch() {
        return logOutputSwitcher.isSwitchRequired(sourcePaths());
    }

    private List<Path> sourcePaths() {
        return properties.targets().stream()
                .map(LogBackupTargetProperties::sourcePath)
                .toList();
    }

    private OperationResult executeBackup() {
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
