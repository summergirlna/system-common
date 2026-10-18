package com.example.systemcommon.resource.cpu.graph.csv;

import com.example.systemcommon.resource.cpu.graph.CpuUsageDataPoint;
import com.example.systemcommon.resource.cpu.graph.CpuUsageDataReader;
import com.example.systemcommon.resource.monitor.ResourceMonitorProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CsvCpuUsageDataReader implements CpuUsageDataReader {

    private static final String CPU_CSV_FILE_PATTERN = "cpu-*.csv";

    private static final int COLLECTED_AT_INDEX = 0;

    private static final int SYSTEM_CPU_LOAD_INDEX = 3;

    private static final int USER_RATE_INDEX = 4;

    private static final int SYSTEM_RATE_INDEX = 5;

    private static final int IDLE_RATE_INDEX = 6;

    private static final int IO_WAIT_RATE_INDEX = 7;

    private static final int MIN_COLUMN_COUNT = 8;

    private final ResourceMonitorProperties properties;

    @Override
    public List<CpuUsageDataPoint> read() {
        Path outputDirectory = properties.outputDirectory();

        if (!Files.isDirectory(outputDirectory)) {
            log.warn("CPUリソース情報CSVディレクトリが存在しません。outputDirectory={}", outputDirectory);
            return List.of();
        }

        try (var stream = Files.newDirectoryStream(outputDirectory, CPU_CSV_FILE_PATTERN)) {
            List<CpuUsageDataPoint> dataPoints = streamToList(stream).stream()
                    .flatMap(path -> readFile(path).stream())
                    .sorted(Comparator.comparing(CpuUsageDataPoint::collectedAt))
                    .toList();

            log.info("CPUリソース情報CSVを読み込みました。outputDirectory={}, count={}", outputDirectory, dataPoints.size());
            return dataPoints;
        } catch (IOException e) {
            log.error("CPUリソース情報CSVの読み込みに失敗しました。outputDirectory={}", outputDirectory, e);
            throw new IllegalStateException("CPUリソース情報CSVの読み込みに失敗しました: " + outputDirectory, e);
        }
    }

    private List<Path> streamToList(Iterable<Path> paths) {
        return java.util.stream.StreamSupport.stream(paths.spliterator(), false)
                .sorted()
                .toList();
    }

    private List<CpuUsageDataPoint> readFile(Path path) {
        try {
            return Files.readAllLines(path).stream()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(line -> parseLine(path, line))
                    .toList();
        } catch (IOException e) {
            log.error("CPUリソース情報CSVファイルの読み込みに失敗しました。path={}", path, e);
            throw new IllegalStateException("CPUリソース情報CSVファイルの読み込みに失敗しました: " + path, e);
        }
    }

    private CpuUsageDataPoint parseLine(Path path, String line) {
        String[] columns = line.split(",", -1);
        if (columns.length < MIN_COLUMN_COUNT) {
            throw new IllegalStateException("CPUリソース情報CSVの列数が不足しています。path=" + path + ", line=" + line);
        }

        return new CpuUsageDataPoint(
                Instant.parse(columns[COLLECTED_AT_INDEX]),
                toPercent(columns[SYSTEM_CPU_LOAD_INDEX]),
                toPercent(columns[USER_RATE_INDEX]),
                toPercent(columns[SYSTEM_RATE_INDEX]),
                toPercent(columns[IDLE_RATE_INDEX]),
                toPercent(columns[IO_WAIT_RATE_INDEX]));
    }

    private double toPercent(String value) {
        return Double.parseDouble(value) * 100.0;
    }
}
