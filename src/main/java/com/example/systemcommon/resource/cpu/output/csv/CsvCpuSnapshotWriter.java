package com.example.systemcommon.resource.cpu.output.csv;

import com.example.systemcommon.resource.cpu.collect.CpuSnapshot;
import com.example.systemcommon.resource.cpu.output.CpuSnapshotWriter;
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
public class CsvCpuSnapshotWriter implements CpuSnapshotWriter {

    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());

    private static final String HEADER = String.join(
            ",",
            "collectedAt",
            "logicalProcessorCount",
            "physicalProcessorCount",
            "systemCpuLoad",
            "userRate",
            "systemRate",
            "idleRate",
            "ioWaitRate",
            "systemLoadAverage1m",
            "systemLoadAverage5m",
            "systemLoadAverage15m");

    private final ResourceMonitorProperties properties;

    @Override
    public void write(CpuSnapshot snapshot) {
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

            log.debug("CPUリソース情報をCSVへ出力しました。outputPath={}", outputPath);

        } catch (IOException e) {
            log.error("CPUリソース情報のCSV出力に失敗しました。outputPath={}", outputPath, e);
            throw new IllegalStateException("CPUリソース情報のCSV出力に失敗しました: " + outputPath, e);
        }
    }

    private Path resolveOutputPath(CpuSnapshot snapshot) {
        String date = FILE_DATE_FORMATTER.format(snapshot.collectedAt());
        return properties.outputDirectory().resolve("cpu-" + date + ".csv");
    }

    private String toCsvLine(CpuSnapshot snapshot) {
        return String.join(
                ",",
                snapshot.collectedAt().toString(),
                String.valueOf(snapshot.logicalProcessorCount()),
                String.valueOf(snapshot.physicalProcessorCount()),
                format(snapshot.systemCpuLoad()),
                format(snapshot.userRate()),
                format(snapshot.systemRate()),
                format(snapshot.idleRate()),
                format(snapshot.ioWaitRate()),
                format(snapshot.systemLoadAverage1m()),
                format(snapshot.systemLoadAverage5m()),
                format(snapshot.systemLoadAverage15m()));
    }

    private String format(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }
}
