package com.example.systemcommon.logbackup.targz;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupPathResolver;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.LogCompressExecutor;
import com.example.systemcommon.operationdate.OperationDateProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TarGzLogCompressExecutor implements LogCompressExecutor {

    private final LogBackupProperties properties;

    private final OperationDateProvider operationDateProvider;

    private final LogBackupPathResolver pathResolver;

    @Override
    public OperationResult compress() {
        log.info("ログ圧縮処理を開始します。");

        Path archiveRootDirectory = properties.archiveRootDirectory();
        if (!Files.isDirectory(archiveRootDirectory)) {
            log.error("ログ退避先ルートディレクトリが存在しません。archiveRootDirectory={}", archiveRootDirectory);
            return OperationResult.failure("ログ退避先ルートディレクトリが存在しません: " + archiveRootDirectory);
        }

        LocalDate operationDate = operationDateProvider.getOperationDate();
        LocalDate compressThresholdDate = operationDate.minusDays(properties.compressAfterDays());

        try (var stream = Files.list(archiveRootDirectory)) {
            for (Path dateDirectory : stream.filter(Files::isDirectory).toList()) {
                OperationResult result = compressIfTarget(dateDirectory, compressThresholdDate);
                if (result.isFailure()) {
                    return result;
                }
            }
        } catch (IOException e) {
            log.error("ログ圧縮対象ディレクトリの走査に失敗しました。archiveRootDirectory={}", archiveRootDirectory);
            return OperationResult.failure("ログ圧縮対象ディレクトリの走査に失敗しました: " + archiveRootDirectory);
        }

        log.info("ログ圧縮処理が正常終了しました。");
        return OperationResult.success("ログ圧縮処理が正常終了しました");
    }

    private OperationResult compressIfTarget(Path dateDirectory, LocalDate compressThresholdDate) {
        return pathResolver
                .parseDateDirectoryName(dateDirectory)
                .map(directoryDate -> compressIfExpired(dateDirectory, directoryDate, compressThresholdDate))
                .orElseGet(() -> {
                    log.info("日付ディレクトリではないためスキップします。directory={}", dateDirectory);
                    return OperationResult.success("日付ディレクトリではないためスキップしました: " + dateDirectory);
                });
    }

    private OperationResult compressIfExpired(
            Path dateDirectory, LocalDate directoryDate, LocalDate compressThresholdDate) {
        if (directoryDate.isAfter(compressThresholdDate)) {
            log.info(
                    "圧縮対象日より新しいためスキップします。directory={}, directoryDate={}, thresholdDate={}",
                    dateDirectory,
                    directoryDate,
                    compressThresholdDate);
            return OperationResult.success("圧縮対象日より新しいためスキップしました: " + dateDirectory);
        }

        return compressDateDirectory(dateDirectory);
    }

    private OperationResult compressDateDirectory(Path dateDirectory) {
        Path tarGzPath = pathResolver.resolveTarGzPath(dateDirectory);
        if (Files.exists(tarGzPath)) {
            log.error("圧縮先ファイルが既に存在します。tarGzPath={}", tarGzPath);
            return OperationResult.failure("圧縮先ファイルが既に存在します: " + tarGzPath);
        }

        try {
            log.info("日付ディレクトリを圧縮します。dateDirectory={}, tarGzPath={}", dateDirectory, tarGzPath);
            createTarGz(dateDirectory, tarGzPath);
            deleteDirectory(dateDirectory);
            log.info("日付ディレクトリの圧縮が完了しました。dateDirectory={}, tarGzPath={}", dateDirectory, tarGzPath);
            return OperationResult.success("日付ディレクトリの圧縮が完了しました: " + dateDirectory);

        } catch (IOException e) {
            log.error("日付ディレクトリの圧縮に失敗しました。dateDirectory={}, tarGzPath={}", dateDirectory, tarGzPath);
            return OperationResult.failure("日付ディレクトリの圧縮に失敗しました: " + dateDirectory);
        }
    }

    private void createTarGz(Path sourceDirectory, Path tarGzPath) throws IOException {
        try (OutputStream fileOutputStream =
                        Files.newOutputStream(tarGzPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                GzipCompressorOutputStream gzipOutputStream = new GzipCompressorOutputStream(fileOutputStream);
                TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(gzipOutputStream)) {

            tarOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            addToTar(sourceDirectory, sourceDirectory.getParent(), tarOutputStream);
        }
    }

    private void addToTar(Path path, Path baseDirectory, TarArchiveOutputStream tarOutputStream) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(@NonNull Path directory, @NonNull BasicFileAttributes attrs)
                    throws IOException {
                addEntry(directory, baseDirectory, tarOutputStream);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs)
                    throws IOException {
                addEntry(file, baseDirectory, tarOutputStream);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void addEntry(Path path, Path baseDirectory, TarArchiveOutputStream tarOutputStream) throws IOException {
        Path relativePath = baseDirectory.relativize(path);
        String entryName = relativePath.toString().replace('\\', '/');

        TarArchiveEntry entry = new TarArchiveEntry(path, entryName);
        tarOutputStream.putArchiveEntry(entry);

        if (Files.isRegularFile(path)) {
            Files.copy(path, tarOutputStream);
        }

        tarOutputStream.closeArchiveEntry();
    }

    private void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(@NonNull Path directory, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }

                Files.delete(directory);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
