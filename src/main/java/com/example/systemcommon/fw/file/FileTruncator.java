package com.example.systemcommon.fw.file;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.stereotype.Component;

@Component
public class FileTruncator {

    public void truncate(Path sourcePath) throws IOException {
        try (FileChannel channel = FileChannel.open(sourcePath, StandardOpenOption.WRITE)) {
            channel.truncate(0);
        }
    }
}
