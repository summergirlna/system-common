package com.example.systemcommon.logbackup.file;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupExecutor;
import com.example.systemcommon.logbackup.LogBackupPathResolver;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.LogBackupTargetProperties;
import com.example.systemcommon.operationdate.OperationDateProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileLogBackupExecutor implements LogBackupExecutor {

    private static final DateTimeFormatter BACKUP_FILE_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final LogBackupProperties properties;

    private final OperationDateProvider operationDateProvider;

    private final LogBackupPathResolver pathResolver;

    private final ExistingBackupFileRenamer existingBackupFileRenamer;

    private final LogFileTruncator logFileTruncator;

    @Override
    public OperationResult backup() {
        log.info("ログ退避処理を開始します。");

        Path archiveRootDirectory = properties.archiveRootDirectory();
        if (!Files.isDirectory(archiveRootDirectory)) {
            log.error("ログ退避先ルートディレクトリが存在しません。archiveRootDirectory={}", archiveRootDirectory);
            return OperationResult.failure("ログ退避先ルートディレクトリが存在しません: " + archiveRootDirectory);
        }

        LocalDate operationDate = operationDateProvider.getOperationDate();

        for (LogBackupTargetProperties target : properties.targets()) {
            OperationResult result = backupTarget(archiveRootDirectory, operationDate, target);
            if (result.isFailure()) {
                return result;
            }
        }

        log.info("ログ退避処理が正常終了しました。");
        return OperationResult.success("ログ退避処理が正常終了しました。");
    }

    private OperationResult backupTarget(
            Path archiveRootDirectory, LocalDate operationDate, LogBackupTargetProperties target) {

        Path sourcePath = target.sourcePath();
        String productName = target.productName();

        if (!Files.exists(sourcePath)) {
            log.info("ログファイルが存在しないためスキップします。productName={}, sourcePath={}", productName, sourcePath);
            return OperationResult.success("ログファイルが存在しないためスキップしました: " + sourcePath);
        }

        if (!Files.isRegularFile(sourcePath)) {
            log.error("ログ退避対象が通常ファイルではありません。productName={}, sourcePath={}", productName, sourcePath);
            return OperationResult.failure("ログ退避対象が通常ファイルではありません: " + sourcePath);
        }

        Path backupFilePath = pathResolver.resolveBackupFilePath(archiveRootDirectory, operationDate, target);

        try {
            Files.createDirectories(backupFilePath.getParent());
            existingBackupFileRenamer.renameIfExists(backupFilePath);

            log.info("ログファイルを退避します。sourcePath={}, backupFilePath={}", sourcePath, backupFilePath);
            Files.copy(sourcePath, backupFilePath, StandardCopyOption.COPY_ATTRIBUTES);

            logFileTruncator.truncate(sourcePath);

            return OperationResult.success("ログファイルの退避が完了しました: " + sourcePath);

        } catch (IOException e) {
            log.error("ログファイルの退避に失敗しました。sourcePath={}, backupFilePath={}", sourcePath, backupFilePath, e);
            return OperationResult.failure("ログファイルの退避に失敗しました: " + sourcePath);
        }
    }
}
