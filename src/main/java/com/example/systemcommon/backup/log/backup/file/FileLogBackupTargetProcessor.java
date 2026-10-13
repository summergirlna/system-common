package com.example.systemcommon.backup.log.backup.file;

import com.example.systemcommon.backup.log.LogBackupPathResolver;
import com.example.systemcommon.backup.log.backup.LogBackupTargetProperties;
import com.example.systemcommon.fw.file.FileTruncator;
import com.example.systemcommon.fw.file.TimestampFileRenamer;
import com.example.systemcommon.fw.operation.OperationResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileLogBackupTargetProcessor {

    private final LogBackupPathResolver pathResolver;

    private final TimestampFileRenamer timestampFileRenamer;

    private final FileTruncator fileTruncator;

    public OperationResult process(
            Path archiveRootDirectory, LocalDate operationDate, LogBackupTargetProperties target) {
        Path sourcePath = target.sourcePath();
        String productName = target.productName();

        // ログが出力されていないこともありえるため、元ログが存在しない場合はスキップする。
        if (!Files.exists(sourcePath)) {
            log.info("ログファイルが存在しないためスキップします。productName={}, sourcePath={}", productName, sourcePath);
            return OperationResult.success("ログファイルが存在しないためスキップしました: " + sourcePath);
        }

        // ディレクトリ等を誤って指定している場合は設定誤りとして異常終了させる。
        // TODO 将来的にはディレクトリ指定、ワイルドカード指定も検討する。
        if (!Files.isRegularFile(sourcePath)) {
            log.error("ログ退避対象が通常ファイルではありません。productName={}, sourcePath={}", productName, sourcePath);
            return OperationResult.failure("ログ退避対象が通常ファイルではありません: " + sourcePath);
        }

        Path backupFilePath = pathResolver.resolveBackupFilePath(archiveRootDirectory, operationDate, target);

        try {
            // 退避先の日付ディレクトリおよびプロダクト名ディレクトリを作成する。
            Files.createDirectories(backupFilePath.getParent());

            // 同一運用日に再実行された場合、既存の退避済みファイルをリネームする。
            timestampFileRenamer.renameIfExists(backupFilePath);

            // 元ログを退避先へコピーする。0バイトファイルでも退避対象とする。
            log.info("ログファイルを退避します。sourcePath={}, backupFilePath={}", sourcePath, backupFilePath);
            Files.copy(sourcePath, backupFilePath, StandardCopyOption.COPY_ATTRIBUTES);

            // コピー成功後、元ログを0バイトクリアする。
            fileTruncator.truncate(sourcePath);

            return OperationResult.success("ログファイルの退避が完了しました: " + sourcePath);
        } catch (IOException e) {
            log.error("ログファイルの退避に失敗しました。sourcePath={}, backupFilePath={}", sourcePath, backupFilePath, e);
            return OperationResult.failure("ログファイルの退避に失敗しました: " + sourcePath);
        }
    }
}
