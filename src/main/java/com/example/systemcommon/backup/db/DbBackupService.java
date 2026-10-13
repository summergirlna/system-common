package com.example.systemcommon.backup.db;

import com.example.systemcommon.backup.db.backup.DbBackupProcessor;
import com.example.systemcommon.backup.db.quiesce.DbQuiesceManager;
import com.example.systemcommon.backup.db.validate.DbBackupValidator;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.operationdate.OperationDateProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DbBackupService {

    private final DbBackupProperties properties;

    private final OperationDateProvider operationDateProvider;

    private final DbQuiesceManager quiesceManager;

    private final DbBackupProcessor backupProcessor;

    private final DbBackupValidator backupValidator;

    public OperationResult backup() {
        log.info("DBバックアップ処理を開始します。");

        OperationResult directoryValidationResult = validateBackupRootDirectory();
        if (directoryValidationResult.isFailure()) {
            return directoryValidationResult;
        }

        DbBackupContext context = createContext();

        OperationResult acquireResult = quiesceManager.acquire(context);
        if (acquireResult.isFailure()) {
            log.error("DB静止点取得に失敗しました。message={}", acquireResult.message());
            return acquireResult;
        }

        BackupExecutionResult executionResult = executeBackupAndValidate(context);

        OperationResult releaseResult = quiesceManager.release(context);
        if (releaseResult.isFailure()) {
            log.error("DB静止点解除に失敗しました。message={}", releaseResult.message());
            return OperationResult.failure("DB静止点解除に失敗しました: " + releaseResult.message());
        }

        return resolveResult(executionResult);
    }

    private OperationResult validateBackupRootDirectory() {
        Path backupRootDirectory = properties.backupRootDirectory();
        if (Files.isDirectory(backupRootDirectory)) {
            return OperationResult.success("DBバックアップ先ルートディレクトリが存在します。");
        }

        log.error("DBバックアップ先ルートディレクトリが存在しません。backupRootDirectory={}", backupRootDirectory);
        return OperationResult.failure("DBバックアップ先ルートディレクトリが存在しません: " + backupRootDirectory);
    }

    private DbBackupContext createContext() {
        LocalDate operationDate = operationDateProvider.getOperationDate();
        return new DbBackupContext(operationDate, properties.backupRootDirectory(), createBackupId(operationDate));
    }

    private String createBackupId(LocalDate operationDate) {
        return operationDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    private BackupExecutionResult executeBackupAndValidate(DbBackupContext context) {
        OperationResult backupResult = backupProcessor.backup(context);
        if (backupResult.isFailure()) {
            log.error("DBバックアップ取得に失敗しました。message={}", backupResult.message());
            return new BackupExecutionResult(backupResult, OperationResult.success("DBバックアップ検証は未実行です。"));
        }

        OperationResult validateResult = backupValidator.validate(context);
        if (validateResult.isFailure()) {
            log.error("DBバックアップ検証に失敗しました。message={}", validateResult.message());
        }

        return new BackupExecutionResult(backupResult, validateResult);
    }

    private OperationResult resolveResult(BackupExecutionResult executionResult) {
        if (executionResult.backupResult().isFailure()) {
            return executionResult.backupResult();
        }

        if (executionResult.validateResult().isFailure()) {
            return executionResult.validateResult();
        }

        log.info("DBバックアップ処理が正常終了しました。");
        return OperationResult.success("DBバックアップ処理が正常終了しました。");
    }

    private record BackupExecutionResult(OperationResult backupResult, OperationResult validateResult) {}
}
