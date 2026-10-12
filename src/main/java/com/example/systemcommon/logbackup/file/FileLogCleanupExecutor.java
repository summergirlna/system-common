package com.example.systemcommon.logbackup.file;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.LogCleanupExecutor;
import com.example.systemcommon.logbackup.LogCleanupTarget;
import com.example.systemcommon.logbackup.LogCleanupTargetResolver;
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
public class FileLogCleanupExecutor implements LogCleanupExecutor {

    private final LogBackupProperties properties;

    private final OperationDateProvider operationDateProvider;

    private final LogCleanupTargetResolver targetResolver;

    private final FileLogCleanupTargetProcessor targetProcessor;

    @Override
    public OperationResult cleanup() {
        log.info("ログ削除処理を開始します。");

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

        // log_arc/20261012.tar.gz のようなファイルを走査し、
        // 保持期限を過ぎていれば削除する。
        try {
            for (LogCleanupTarget target :
                    targetResolver.resolve(archiveRootDirectory, operationDate, properties.deleteAfterDays())) {
                OperationResult result = targetProcessor.process(target);
                if (result.isFailure()) {
                    return result;
                }
            }
        } catch (IOException e) {
            log.error("ログ削除対象ファイルの解決に失敗しました。archiveRootDirectory={}", archiveRootDirectory, e);
            return OperationResult.failure("ログ削除対象ファイルの解決に失敗しました: " + archiveRootDirectory);
        }

        log.info("ログ削除処理が正常終了しました。");
        return OperationResult.success("ログ削除処理が正常終了しました。");
    }
}
