package com.example.systemcommon.logbackup.backup.file;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.backup.LogBackupExecutor;
import com.example.systemcommon.logbackup.backup.LogBackupTargetProperties;
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
public class FileLogBackupExecutor implements LogBackupExecutor {

    private static final DateTimeFormatter BACKUP_FILE_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final LogBackupProperties properties;

    private final OperationDateProvider operationDateProvider;

    private final FileLogBackupTargetProcessor targetProcessor;

    @Override
    public OperationResult backup() {
        log.info("ログ退避処理を開始します。");

        // 退避先ルートディレクトリは事前に作成しておく前提。
        // 存在しない場合は設定誤りまたは運用準備不足として異常終了させる。
        Path archiveRootDirectory = properties.archiveRootDirectory();
        if (!Files.isDirectory(archiveRootDirectory)) {
            log.error("ログ退避先ルートディレクトリが存在しません。archiveRootDirectory={}", archiveRootDirectory);
            return OperationResult.failure("ログ退避先ルートディレクトリが存在しません: " + archiveRootDirectory);
        }

        // システム日付だと日付跨り処理をした際にわかりにくいので、
        // 常に運用日付を起点にする。
        LocalDate operationDate = operationDateProvider.getOperationDate();

        // 設定された退避対象を順番に処理する。
        // 1件でも失敗した場合は、後続を処理せずに異常終了させる。
        for (LogBackupTargetProperties target : properties.targets()) {
            OperationResult result = targetProcessor.process(archiveRootDirectory, operationDate, target);
            if (result.isFailure()) {
                return result;
            }
        }

        log.info("ログ退避処理が正常終了しました。");
        return OperationResult.success("ログ退避処理が正常終了しました。");
    }
}
