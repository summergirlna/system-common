package com.example.systemcommon.logbackup.targz;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupPathResolver;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.LogCompressExecutor;
import com.example.systemcommon.logbackup.LogCompressTarget;
import com.example.systemcommon.logbackup.LogCompressTargetResolver;
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

    private final LogCompressTargetResolver targetResolver;

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

        try {
            for (LogCompressTarget target :
                    targetResolver.resolve(archiveRootDirectory, operationDate, properties.compressAfterDays())) {
                OperationResult result = compressTarget(target);
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

    private OperationResult compressTarget(LogCompressTarget target) {
        Path dateDirectory = target.dateDirectory();
        Path tarGzPath = pathResolver.resolveTarGzPath(dateDirectory);
        if (Files.exists(tarGzPath)) {
            log.error("圧縮先ファイルが既に存在します。tarGzPath={}", tarGzPath);
            return OperationResult.failure("圧縮先ファイルが既に存在します: " + tarGzPath);
        }

        try {
            log.info("日付ディレクトリを圧縮します。dateDirectory={}, tarGzPath={}", target, tarGzPath);
            archiveCreator.create(dateDirectory, tarGzPath);
            directoryDeleter.delete(dateDirectory);
            log.info("日付ディレクトリの圧縮が完了しました。dateDirectory={}, tarGzPath={}", target, tarGzPath);
            return OperationResult.success("日付ディレクトリの圧縮が完了しました: " + target);

        } catch (IOException e) {
            log.error("日付ディレクトリの圧縮に失敗しました。dateDirectory={}, tarGzPath={}", target, tarGzPath);
            return OperationResult.failure("日付ディレクトリの圧縮に失敗しました: " + target);
        }
    }
}
