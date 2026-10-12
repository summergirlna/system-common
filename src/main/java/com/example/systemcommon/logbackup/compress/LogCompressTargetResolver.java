package com.example.systemcommon.logbackup.compress;

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
public class LogCompressTargetResolver {

    private final LogBackupPathResolver pathResolver;

    public List<LogCompressTarget> resolve(Path archiveRootDirectory, LocalDate operationDate, int compressAfterDays)
            throws IOException {
        LocalDate thresholdDate = operationDate.minusDays(compressAfterDays);

        try (var stream = Files.list(archiveRootDirectory)) {
            return stream.filter(Files::isDirectory)
                    .flatMap(directory -> resolveTarget(directory, thresholdDate).stream())
                    .toList();
        }
    }

    private Optional<LogCompressTarget> resolveTarget(Path directory, LocalDate thresholdDate) {
        Optional<LocalDate> directoryDate = pathResolver.parseDateDirectoryName(directory);
        if (directoryDate.isEmpty()) {
            log.info("日付ディレクトリではないためスキップします。directory={}", directory);
            return Optional.empty();
        }

        return resolveTarget(directory, directoryDate.get(), thresholdDate);
    }

    private Optional<LogCompressTarget> resolveTarget(
            Path directory, LocalDate directoryDate, LocalDate thresholdDate) {
        if (directoryDate.isAfter(thresholdDate)) {
            log.info(
                    "圧縮対象日より新しいためスキップします。directory={}, directoryDate={}, thresholdDate={}",
                    directory,
                    directoryDate,
                    thresholdDate);
            return Optional.empty();
        }

        return Optional.of(new LogCompressTarget(directory, directoryDate));
    }
}
