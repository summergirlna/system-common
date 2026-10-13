package com.example.systemcommon.backup.log.backup.file;

import com.example.systemcommon.backup.log.LogArchiveProperties;
import com.example.systemcommon.backup.log.backup.LogBackupTargetProperties;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.operationdate.OperationDateProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileLogBackupService {
    private final OperationDateProvider operationDateProvider;

    private final FileLogBackupTargetProcessor targetProcessor;

    public OperationResult backup(LogArchiveProperties properties) {
        log.info("ログ退避処理を開始します。");

        Path archiveRootDirectory = properties.archiveRootDirectory();
        if (!Files.isDirectory(archiveRootDirectory)) {
            log.error("ログ退避先ルートディレクトリが存在しません。archiveRootDirectory={}", archiveRootDirectory);
            return OperationResult.failure("ログ退避先ルートディレクトリが存在しません: " + archiveRootDirectory);
        }

        LocalDate operationDate = operationDateProvider.getOperationDate();

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
