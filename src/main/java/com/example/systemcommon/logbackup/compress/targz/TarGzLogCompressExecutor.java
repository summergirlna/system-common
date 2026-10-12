package com.example.systemcommon.logbackup.compress.targz;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.compress.LogCompressExecutor;
import com.example.systemcommon.logbackup.compress.LogCompressTarget;
import com.example.systemcommon.logbackup.compress.LogCompressTargetResolver;
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

    private final LogCompressTargetResolver targetResolver;

    private final TarGzLogCompressTargetProcessor targetProcessor;

    @Override
    public OperationResult compress() {
        log.info("ログ圧縮処理を開始します。");

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

        // 日付ディレクトリを走査し、圧縮する対象があれば圧縮する。
        // 1件でも失敗した場合は、後続を処理せずに異常終了させる。
        try {
            for (LogCompressTarget target :
                    targetResolver.resolve(archiveRootDirectory, operationDate, properties.compressAfterDays())) {
                OperationResult result = targetProcessor.process(target);
                if (result.isFailure()) {
                    return result;
                }
            }
        } catch (IOException e) {
            log.error("ログ圧縮対象ディレクトリの走査に失敗しました。archiveRootDirectory={}", archiveRootDirectory);
            return OperationResult.failure("ログ圧縮対象ディレクトリの走査に失敗しました: " + archiveRootDirectory);
        }

        log.info("ログ圧縮処理が正常終了しました。");
        return OperationResult.success("ログ圧縮処理が正常終了しました。");
    }
}
