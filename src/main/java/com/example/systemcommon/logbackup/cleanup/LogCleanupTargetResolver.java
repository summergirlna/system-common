package com.example.systemcommon.logbackup.cleanup;

import com.example.systemcommon.logbackup.LogBackupPathResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogCleanupTargetResolver {

    private final LogBackupPathResolver pathResolver;

    public List<LogCleanupTarget> resolve(Path archiveRootDirectory, LocalDate operationDate, int deleteAfterDays)
            throws IOException {
        LocalDate thresholdDate = operationDate.minusDays(deleteAfterDays);

        try (var stream = Files.list(archiveRootDirectory)) {
            return stream.filter(Files::isRegularFile)
                    .flatMap(path -> resolveTarget(path, thresholdDate).stream())
                    .toList();
        }
    }

    private Optional<LogCleanupTarget> resolveTarget(Path path, LocalDate thresholdDate) {
        Optional<LocalDate> tarGzDate = pathResolver.parseTarGzFileName(path);
        if (tarGzDate.isEmpty()) {
            log.info("削除対象の圧縮ログファイルではないためスキップします。path={}", path);
            return Optional.empty();
        }

        return resolveTarget(path, tarGzDate.get(), thresholdDate);
    }

    private Optional<LogCleanupTarget> resolveTarget(Path path, LocalDate tarGzDate, LocalDate thresholdDate) {
        if (tarGzDate.isAfter(thresholdDate)) {
            log.info("削除対象日より新しいためスキップします。path={}, tarGzDate={}, thresholdDate={}", path, tarGzDate, thresholdDate);
            return Optional.empty();
        }

        return Optional.of(new LogCleanupTarget(path, tarGzDate));
    }
}
