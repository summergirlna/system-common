package com.example.systemcommon.backup.log.compress.targz;

import com.example.systemcommon.backup.log.LogBackupPathResolver;
import com.example.systemcommon.backup.log.compress.LogCompressTarget;
import com.example.systemcommon.fw.file.DirectoryDeleter;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.targz.TarGzArchiveCreator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TarGzLogCompressTargetProcessor {

    private final LogBackupPathResolver pathResolver;

    private final TarGzArchiveCreator archiveCreator;

    private final DirectoryDeleter directoryDeleter;

    public OperationResult process(LogCompressTarget target) {
        // ex. log_arc/20261012
        Path dateDirectory = target.dateDirectory();
        // ex. log_arc/20261012.tar.gz
        Path tarGzPath = pathResolver.resolveTarGzPath(dateDirectory);

        // 圧縮先ファイルが既に存在する場合は異常終了させる。
        if (Files.exists(tarGzPath)) {
            log.error("圧縮先ファイルが既に存在します。tarGzPath={}", tarGzPath);
            return OperationResult.failure("圧縮先ファイルが既に存在します: " + tarGzPath);
        }

        // log_arc/20261012 を圧縮し、元ディレクトリは削除する。
        try {
            log.info("日付ディレクトリを圧縮します。dateDirectory={}, tarGzPath={}", dateDirectory, tarGzPath);
            archiveCreator.create(dateDirectory, tarGzPath);
            directoryDeleter.delete(dateDirectory);
            log.info("日付ディレクトリの圧縮が完了しました。dateDirectory={}, tarGzPath={}", dateDirectory, tarGzPath);
            return OperationResult.success("日付ディレクトリの圧縮が完了しました: " + dateDirectory);
        } catch (IOException e) {
            log.error("日付ディレクトリの圧縮に失敗しました。dateDirectory={}, tarGzPath={}", dateDirectory, tarGzPath, e);
            return OperationResult.failure("日付ディレクトリの圧縮に失敗しました: " + dateDirectory);
        }
    }
}
