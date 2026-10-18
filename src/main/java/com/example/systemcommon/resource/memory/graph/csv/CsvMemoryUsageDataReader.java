package com.example.systemcommon.resource.memory.graph.csv;

import com.example.systemcommon.resource.memory.graph.MemoryUsageDataPoint;
import com.example.systemcommon.resource.memory.graph.MemoryUsageDataReader;
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
public class CsvMemoryUsageDataReader implements MemoryUsageDataReader {
    private static final String MEMORY_CSV_FILE_PATTERN = "memory-*.csv";

    private static final int COLLECTED_AT_INDEX = 0;

    private static final int USED_MEMORY_RATE_INDEX = 4;

    private static final int USED_SWAP_RATE_INDEX = 7;

    private static final int MIN_COLUMN_COUNT = 8;

    private final ResourceMonitorProperties properties;

    @Override
    public List<MemoryUsageDataPoint> read() {
        Path outputDirectory = properties.outputDirectory();

        if (!Files.isDirectory(outputDirectory)) {
            log.warn("メモリリソース情報CSVディレクトリが存在しません。outputDirectory={}", outputDirectory);
            return List.of();
        }

        try (var stream = Files.newDirectoryStream(outputDirectory, MEMORY_CSV_FILE_PATTERN)) {
            List<MemoryUsageDataPoint> dataPoints = streamToList(stream).stream()
                    .flatMap(path -> readFile(path).stream())
                    .sorted(Comparator.comparing(MemoryUsageDataPoint::collectedAt))
                    .toList();

            log.info("メモリリソース情報CSVを読み込みました。outputDirectory={}, count={}", outputDirectory, dataPoints.size());
            return dataPoints;

        } catch (IOException e) {
            log.error("メモリリソース情報CSVの読み込みに失敗しました。outputDirectory={}", outputDirectory, e);
            throw new IllegalStateException("メモリリソース情報CSVの読み込みに失敗しました: " + outputDirectory, e);
        }
    }

    private List<Path> streamToList(Iterable<Path> paths) {
        return java.util.stream.StreamSupport.stream(paths.spliterator(), false)
                .sorted()
                .toList();
    }

    private List<MemoryUsageDataPoint> readFile(Path path) {
        try {
            return Files.readAllLines(path).stream()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(line -> parseLine(path, line))
                    .toList();

        } catch (IOException e) {
            log.error("メモリリソース情報CSVファイルの読み込みに失敗しました。path={}", path, e);
            throw new IllegalStateException("メモリリソース情報CSVファイルの読み込みに失敗しました: " + path, e);
        }
    }

    private MemoryUsageDataPoint parseLine(Path path, String line) {
        String[] columns = line.split(",", -1);
        if (columns.length < MIN_COLUMN_COUNT) {
            throw new IllegalStateException("メモリリソース情報CSVの列数が不足しています。path=" + path + ", line=" + line);
        }

        return new MemoryUsageDataPoint(
                Instant.parse(columns[COLLECTED_AT_INDEX]),
                toPercent(columns[USED_MEMORY_RATE_INDEX]),
                toPercent(columns[USED_SWAP_RATE_INDEX]));
    }

    private double toPercent(String value) {
        return Double.parseDouble(value) * 100.0;
    }
}
