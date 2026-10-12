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

        // コピー元ログのパス)(ex. log/system-common.log)
        Path sourcePath = target.sourcePath();
        // コピー元ログのプロダクト名(ex. application)
        String productName = target.productName();

        // ログが出力されていないこともありえるため、元ログが存在しない場合はスキップする。
        if (!Files.exists(sourcePath)) {
            log.info("ログファイルが存在しないためスキップします。productName={}, sourcePath={}", productName, sourcePath);
            return OperationResult.success("ログファイルが存在しないためスキップしました: " + sourcePath);
        }

        // ディレクトリ等を誤って指定している場合は設定誤りとして異常終了させる。
        // todo 将来的にはディレクトリ指定あり、ワイルドカード指定ありにすべき。
        if (!Files.isRegularFile(sourcePath)) {
            log.error("ログ退避対象が通常ファイルではありません。productName={}, sourcePath={}", productName, sourcePath);
            return OperationResult.failure("ログ退避対象が通常ファイルではありません: " + sourcePath);
        }

        // ex. log_arc/20261012/application/system-common.log
        Path backupFilePath = pathResolver.resolveBackupFilePath(archiveRootDirectory, operationDate, target);

        try {
            // log_arc/20261012/application を作成する
            Files.createDirectories(backupFilePath.getParent());

            // 同一運用日に再実行された場合、ログを上書きせず、
            // 元ファイルをタイムスタンプ付きファイル名にリネームする。
            existingBackupFileRenamer.renameIfExists(backupFilePath);

            log.info("ログファイルを退避します。sourcePath={}, backupFilePath={}", sourcePath, backupFilePath);
            Files.copy(sourcePath, backupFilePath, StandardCopyOption.COPY_ATTRIBUTES);

            // コピーに成功した後、元ファイルは0バイトクリア。
            logFileTruncator.truncate(sourcePath);

            return OperationResult.success("ログファイルの退避が完了しました: " + sourcePath);

        } catch (IOException e) {
            log.error("ログファイルの退避に失敗しました。sourcePath={}, backupFilePath={}", sourcePath, backupFilePath, e);
            return OperationResult.failure("ログファイルの退避に失敗しました: " + sourcePath);
        }
    }
}
