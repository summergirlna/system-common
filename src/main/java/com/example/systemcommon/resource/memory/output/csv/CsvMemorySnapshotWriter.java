package com.example.systemcommon.resource.memory.output.csv;

import com.example.systemcommon.resource.memory.collect.MemorySnapshot;
import com.example.systemcommon.resource.memory.output.MemorySnapshotWriter;
import com.example.systemcommon.resource.monitor.ResourceMonitorProperties;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CsvMemorySnapshotWriter implements MemorySnapshotWriter {

    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());

    private static final String HEADER = String.join(
            ",",
            "collectedAt",
            "totalMemoryBytes",
            "availableMemoryBytes",
            "usedMemoryBytes",
            "usedMemoryRate",
            "totalSwapBytes",
            "usedSwapBytes",
            "usedSwapRate");

    private final ResourceMonitorProperties properties;

    @Override
    public void write(MemorySnapshot snapshot) {
        Path outputPath = resolveOutputPath(snapshot);

        try {
            Files.createDirectories(outputPath.getParent());

            boolean shouldWriteHeader = Files.notExists(outputPath) || Files.size(outputPath) == 0;
            if (shouldWriteHeader) {
                Files.writeString(
                        outputPath,
                        HEADER + System.lineSeparator(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            }

            Files.writeString(
                    outputPath,
                    toCsvLine(snapshot) + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            log.debug("メモリリソース情報をCSVへ出力しました。outputPath={}", outputPath);
        } catch (IOException e) {
            log.error("メモリリソース情報のCSV出力に失敗しました。outputPath={}", outputPath, e);
            throw new IllegalStateException("メモリリソース情報のCSV出力に失敗しました: " + outputPath, e);
        }
    }

    private Path resolveOutputPath(MemorySnapshot snapshot) {
        String date = FILE_DATE_FORMATTER.format(snapshot.collectedAt());
        return properties.outputDirectory().resolve("memory-" + date + ".csv");
    }

    private String toCsvLine(MemorySnapshot snapshot) {
        return String.join(
                ",",
                snapshot.collectedAt().toString(),
                String.valueOf(snapshot.totalMemoryBytes()),
                String.valueOf(snapshot.availableMemoryBytes()),
                String.valueOf(snapshot.usedMemoryBytes()),
                format(snapshot.usedMemoryRate()),
                String.valueOf(snapshot.totalSwapBytes()),
                String.valueOf(snapshot.usedSwapBytes()),
                format(snapshot.usedSwapRate()));
    }

    private String format(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }
}
