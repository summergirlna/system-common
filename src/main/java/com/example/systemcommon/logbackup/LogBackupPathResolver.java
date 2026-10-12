package com.example.systemcommon.logbackup;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LogBackupPathResolver {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private static final String TAR_GZ_EXTENSION = ".tar.gz";

    public String resolveDateDirectoryName(LocalDate operationDate) {
        return operationDate.format(DATE_FORMATTER);
    }

    public Path resolveBackupFilePath(
            Path archiveRootDirectory, LocalDate operationDate, LogBackupTargetProperties target) {
        return archiveRootDirectory
                .resolve(resolveDateDirectoryName(operationDate))
                .resolve(target.productName())
                .resolve(target.sourcePath().getFileName());
    }

    public Optional<LocalDate> parseDateDirectoryName(Path dateDirectory) {
        return parseDateDirectoryName(dateDirectory.getFileName().toString());
    }

    public Optional<LocalDate> parseDateDirectoryName(String dateDirectoryName) {
        try {
            return Optional.of(LocalDate.parse(dateDirectoryName, DATE_FORMATTER));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public Path resolveTarGzPath(Path dateDirectory) {
        String dateDirectoryName = dateDirectory.getFileName().toString();
        return dateDirectory.resolveSibling(dateDirectoryName + TAR_GZ_EXTENSION);
    }

    public Optional<LocalDate> parseTarGzFileName(Path tarGzPath) {
        return parseTarGzFileName(tarGzPath.getFileName().toString());
    }

    public Optional<LocalDate> parseTarGzFileName(String tarGzFileName) {
        if (!tarGzFileName.endsWith(TAR_GZ_EXTENSION)) {
            return Optional.empty();
        }

        String datePart = tarGzFileName.substring(0, tarGzFileName.length() - TAR_GZ_EXTENSION.length());
        return parseDateDirectoryName(datePart);
    }
}
