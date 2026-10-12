package com.example.systemcommon.logbackup.targz;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupPathResolver;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.LogCompressExecutor;
import com.example.systemcommon.logbackup.file.DirectoryDeleter;
import com.example.systemcommon.operationdate.OperationDateProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TarGzLogCompressExecutor implements LogCompressExecutor {

    private final LogBackupProperties properties;

    private final OperationDateProvider operationDateProvider;

    private final LogBackupPathResolver pathResolver;

    private final TarGzArchiveCreator archiveCreator;

    private final DirectoryDeleter directoryDeleter;

    @Override
    public OperationResult compress() {
        log.info("ログ圧縮処理を開始します。");

        Path archiveRootDirectory = properties.archiveRootDirectory();
        if (!Files.isDirectory(archiveRootDirectory)) {
            log.error("ログ退避先ルートディレクトリが存在しません。archiveRootDirectory={}", archiveRootDirectory);
            return OperationResult.failure("ログ退避先ルートディレクトリが存在しません: " + archiveRootDirectory);
        }

        LocalDate operationDate = operationDateProvider.getOperationDate();
        LocalDate compressThresholdDate = operationDate.minusDays(properties.compressAfterDays());

        try (var stream = Files.list(archiveRootDirectory)) {
            for (Path dateDirectory : stream.filter(Files::isDirectory).toList()) {
                OperationResult result = compressIfTarget(dateDirectory, compressThresholdDate);
                if (result.isFailure()) {
                    return result;
                }
            }
        } catch (IOException e) {
            log.error("ログ圧縮対象ディレクトリの走査に失敗しました。archiveRootDirectory={}", archiveRootDirectory);
            return OperationResult.failure("ログ圧縮対象ディレクトリの走査に失敗しました: " + archiveRootDirectory);
        }

        log.info("ログ圧縮処理が正常終了しました。");
        return OperationResult.success("ログ圧縮処理が正常終了しました");
    }

    private OperationResult compressIfTarget(Path dateDirectory, LocalDate compressThresholdDate) {
        return pathResolver
                .parseDateDirectoryName(dateDirectory)
                .map(directoryDate -> compressIfExpired(dateDirectory, directoryDate, compressThresholdDate))
                .orElseGet(() -> {
                    log.info("日付ディレクトリではないためスキップします。directory={}", dateDirectory);
                    return OperationResult.success("日付ディレクトリではないためスキップしました: " + dateDirectory);
                });
    }

    private OperationResult compressIfExpired(
            Path directory, LocalDate directoryDate, LocalDate compressThresholdDate) {
        if (directoryDate.isAfter(compressThresholdDate)) {
            log.info(
                    "圧縮対象日より新しいためスキップします。directory={}, directoryDate={}, thresholdDate={}",
                    directory,
                    directoryDate,
                    compressThresholdDate);
            return OperationResult.success("圧縮対象日より新しいためスキップしました: " + directory);
        }

        return compressDirectory(directory);
    }

    private OperationResult compressDirectory(Path directory) {
        Path tarGzPath = pathResolver.resolveTarGzPath(directory);
        if (Files.exists(tarGzPath)) {
            log.error("圧縮先ファイルが既に存在します。tarGzPath={}", tarGzPath);
            return OperationResult.failure("圧縮先ファイルが既に存在します: " + tarGzPath);
        }

        try {
            log.info("日付ディレクトリを圧縮します。dateDirectory={}, tarGzPath={}", directory, tarGzPath);
            archiveCreator.create(directory, tarGzPath);
            directoryDeleter.delete(directory);
            log.info("日付ディレクトリの圧縮が完了しました。dateDirectory={}, tarGzPath={}", directory, tarGzPath);
            return OperationResult.success("日付ディレクトリの圧縮が完了しました: " + directory);

        } catch (IOException e) {
            log.error("日付ディレクトリの圧縮に失敗しました。dateDirectory={}, tarGzPath={}", directory, tarGzPath);
            return OperationResult.failure("日付ディレクトリの圧縮に失敗しました: " + directory);
        }
    }
}
