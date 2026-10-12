package com.example.systemcommon.logbackup;

import com.example.systemcommon.fw.operation.OperationResult;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogBackupOperation {

    private final LogBackupExecutor logBackupExecutor;

    private final LogBackupProperties properties;

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
