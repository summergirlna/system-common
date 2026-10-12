package com.example.systemcommon.fw.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimestampFileRenamer {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final Clock clock;

    public void renameIfExists(Path backupFilePath) throws IOException {
        if (!Files.exists(backupFilePath)) {
            return;
        }

        Path renamedBackupFilePath = resolveRenamedBackupFilePath(backupFilePath);

        log.warn(
                "ログ退避先ファイルがすでに存在するためリネームします。backupFilePath={}, renamedBackupFilePath={}",
                backupFilePath,
                renamedBackupFilePath);

        Files.move(backupFilePath, renamedBackupFilePath);
    }

    private Path resolveRenamedBackupFilePath(Path backupFilePath) {
        String timestamp = LocalDateTime.now(clock).format(TIMESTAMP_FORMATTER);
        Path parent = backupFilePath.getParent();
        String fileName = backupFilePath.getFileName().toString();
        Path renamedPath = parent.resolve(fileName + "." + timestamp);

        int sequence = 1;
        while (Files.exists(renamedPath)) {
            renamedPath = parent.resolve(fileName + "." + timestamp + "." + sequence);
            sequence++;
        }

        return renamedPath;
    }
}
