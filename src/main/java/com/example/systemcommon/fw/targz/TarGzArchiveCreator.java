package com.example.systemcommon.fw.targz;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TarGzArchiveCreator {

    public void create(Path sourceDirectory, Path tarGzPath) throws IOException {
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
}
