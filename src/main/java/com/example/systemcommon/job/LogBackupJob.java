package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.Job;
import com.example.systemcommon.fw.job.JobExitCode;
import com.example.systemcommon.fw.job.JobResult;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupExecutor;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.LogBackupTargetProperties;
import com.example.systemcommon.logbackup.LogOutputSwitcher;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogBackupJob implements Job {

    private final LogBackupExecutor logBackupExecutor;

    private final LogBackupProperties properties;

    private final LogOutputSwitcher logOutputSwitcher;

    @Override
    public String name() {
        return "log-backup";
    }

    public JobResult execute() {
        OperationResult result = executeWithLogOutputControl();

        if (result.isFailure()) {
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        return JobResult.success(result.message());
    }

    private OperationResult executeWithLogOutputControl() {
        List<Path> targetPaths = properties.targets().stream()
                .map(LogBackupTargetProperties::sourcePath)
                .toList();

        if (logOutputSwitcher.isSwitchRequired(targetPaths)) {
            return logOutputSwitcher.executeWithTemporaryOutput(this::executeLogBackup);
        }

        return executeLogBackup();
    }

    private OperationResult executeLogBackup() {
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
