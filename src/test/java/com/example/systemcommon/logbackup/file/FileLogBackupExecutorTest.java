package com.example.systemcommon.logbackup.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupPathResolver;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.LogBackupTargetProperties;
import com.example.systemcommon.operationdate.OperationDateProvider;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileLogBackupExecutorTest {

    private static final LocalDate OPERATION_DATE = LocalDate.of(2026, 10, 10);

    @TempDir
    Path tempDir;

    @Test
    void archiveRootDirectoryDoesNotExist() {
        Path archiveRootDirectory = tempDir.resolve("missing-log-arc");
        Path sourcePath = tempDir.resolve("logs/system-common.log");

        FileLogBackupExecutor executor = newExecutor(archiveRootDirectory, sourcePath);

        OperationResult result = executor.backup();

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void sourceLogDoesNotExistThenSkip() throws Exception {
        Path archiveRootDirectory = createDirectory("log_arc");
        Path sourcePath = tempDir.resolve("logs/system-common.log");

        FileLogBackupExecutor executor = newExecutor(archiveRootDirectory, sourcePath);

        OperationResult result = executor.backup();

        assertThat(result.success()).isTrue();
        assertThat(archiveRootDirectory.resolve("20261010/application/system-common.log"))
                .doesNotExist();
    }

    @Test
    void sourcePathIsDirectoryThenFailure() throws Exception {
        Path archiveRootDirectory = createDirectory("log_arc");
        Path sourcePath = createDirectory("logs/system-common.log");

        FileLogBackupExecutor executor = newExecutor(archiveRootDirectory, sourcePath);

        OperationResult result = executor.backup();

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void backupLogAndTruncateSourceLog() throws Exception {
        Path archiveRootDirectory = createDirectory("log_arc");
        Path sourcePath = createFile("logs/system-common.log", "line1%nline2%n");

        FileLogBackupExecutor executor = newExecutor(archiveRootDirectory, sourcePath);

        OperationResult result = executor.backup();

        Path backupPath = archiveRootDirectory.resolve("20261010/application/system-common.log");

        assertThat(result.success()).isTrue();
        assertThat(backupPath).exists();
        assertThat(Files.readString(backupPath, StandardCharsets.UTF_8)).isEqualTo("line1%nline2%n");
        assertThat(Files.size(sourcePath)).isZero();
    }

    @Test
    void backupEmptyLogFile() throws Exception {
        Path archiveRootDirectory = createDirectory("log_arc");
        Path sourcePath = createFile("logs/system-common.log", "");

        FileLogBackupExecutor executor = newExecutor(archiveRootDirectory, sourcePath);

        OperationResult result = executor.backup();

        Path backupPath = archiveRootDirectory.resolve("20261010/application/system-common.log");

        assertThat(result.success()).isTrue();
        assertThat(backupPath).exists();
        assertThat(Files.size(backupPath)).isZero();
        assertThat(Files.size(sourcePath)).isZero();
    }

    @Test
    void existingBackupFileIsRenamedAndNewBackupFileIsCreated() throws Exception {
        Path archiveRootDirectory = createDirectory("log_arc");
        Path sourcePath = createFile("logs/system-common.log", "new log");
        Path existingBackupPath = createFile("log_arc/20261010/application/system-common.log", "old log");

        FileLogBackupExecutor executor = newExecutor(archiveRootDirectory, sourcePath);

        OperationResult result = executor.backup();

        assertThat(result.success()).isTrue();
        assertThat(existingBackupPath).exists();
        assertThat(Files.readString(existingBackupPath, StandardCharsets.UTF_8)).isEqualTo("new log");

        List<Path> renamedFiles;
        try (var stream = Files.list(existingBackupPath.getParent())) {
            renamedFiles = stream.filter(path -> path.getFileName().toString().startsWith("system-common.log."))
                    .toList();
        }

        assertThat(renamedFiles).hasSize(1);
        assertThat(Files.readString(renamedFiles.getFirst(), StandardCharsets.UTF_8))
                .isEqualTo("old log");
        assertThat(Files.size(sourcePath)).isZero();
    }

    private FileLogBackupExecutor newExecutor(Path archiveRootDirectory, Path sourcePath) {
        LogBackupProperties properties = new LogBackupProperties(
                archiveRootDirectory, 7, 90, List.of(new LogBackupTargetProperties("application", sourcePath)));

        OperationDateProvider operationDateProvider = () -> OPERATION_DATE;

        Clock clock = Clock.fixed(Instant.parse("2026-10-10T12:30:45.123Z"), ZoneId.of("Asia/Tokyo"));

        return new FileLogBackupExecutor(
                properties,
                operationDateProvider,
                new LogBackupPathResolver(),
                new ExistingBackupFileRenamer(clock),
                new LogFileTruncator());
    }

    private Path createDirectory(String path) throws Exception {
        Path directory = tempDir.resolve(path);
        Files.createDirectories(directory);
        return directory;
    }

    private Path createFile(String path, String content) throws Exception {
        Path file = tempDir.resolve(path);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
