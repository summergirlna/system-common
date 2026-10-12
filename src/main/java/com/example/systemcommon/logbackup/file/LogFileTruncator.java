package com.example.systemcommon.logbackup.file;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.stereotype.Component;

@Component
public class LogFileTruncator {

    public void truncate(Path sourcePath) throws IOException {
        try (FileChannel channel = FileChannel.open(sourcePath, StandardOpenOption.WRITE)) {
            channel.truncate(0);
        }
    }
}
