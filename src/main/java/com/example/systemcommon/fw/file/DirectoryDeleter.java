package com.example.systemcommon.fw.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class DirectoryDeleter {

    public void delete(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(@NonNull Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
