package com.example.systemcommon.backup.log.cleanup.file;

import com.example.systemcommon.backup.log.cleanup.LogCleanupTarget;
import com.example.systemcommon.fw.operation.OperationResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FileLogCleanupTargetProcessor {

    public OperationResult process(LogCleanupTarget target) {
        // ex. log_arc/20261012.tar.gz
        Path tarGzPath = target.tarGzPath();

        try {
            log.info("圧縮ログファイルを削除します。tarGzPath={}, date={}", tarGzPath, target.date());
            Files.delete(tarGzPath);
            log.info("圧縮ログファイルを削除しました。tarGzPath={}", tarGzPath);
            return OperationResult.success("圧縮ログファイルを削除しました: " + tarGzPath);
        } catch (IOException e) {
            log.error("圧縮ログファイルの削除に失敗しました。tarGzPath={}", tarGzPath, e);
            return OperationResult.failure("圧縮ログファイルの削除に失敗しました: " + tarGzPath);
        }
    }
}
