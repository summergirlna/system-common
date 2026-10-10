package com.example.systemcommon.operationdate;

import com.example.systemcommon.fw.operation.OperationResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileOperationDateService implements OperationDateProvider, OperationDateUpdater {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final OperationDateProperties properties;

    private final Clock clock;

    @Override
    public LocalDate getOperationDate() {
        Path filePath = properties.filePath();

        try {
            String value = Files.readString(filePath, StandardCharsets.UTF_8).trim();
            return LocalDate.parse(value, FORMATTER);
        } catch (IOException e) {
            throw new OperationDateException("運用日付ファイルの読み込みに失敗しました: " + filePath, e);
        } catch (DateTimeParseException e) {
            throw new OperationDateException("運用日付の形式が不正です: " + filePath, e);
        }
    }

    @Override
    public OperationResult update() {
        LocalDate operationDate = LocalDate.now(clock);
        String value = operationDate.format(FORMATTER);
        Path filePath = properties.filePath();

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(filePath, value + System.lineSeparator(), StandardCharsets.UTF_8);

            log.info("運用日付を更新しました。operationDate={}, filePath={}", value, filePath);
            return OperationResult.success("運用日付を更新しました: " + value);
        } catch (IOException e) {
            log.error("運用日付ファイルの更新に失敗しました。filePath={}", filePath, e);
            return OperationResult.failure("運用日付ファイルの更新に失敗しました: " + filePath);
        }
    }
}
