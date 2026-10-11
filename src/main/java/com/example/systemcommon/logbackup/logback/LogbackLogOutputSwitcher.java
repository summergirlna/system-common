package com.example.systemcommon.logbackup.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogOutputSwitcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogbackLogOutputSwitcher implements LogOutputSwitcher {

    private static final String FILE_APPENDER_NAME = "FILE";

    private static final String TEMPORARY_LOG_SUFFIX = ".archive-work";

    @Override
    public boolean isSwitchRequired(List<Path> targetPaths) {
        FileAppender<ILoggingEvent> fileAppender = getFileAppender();
        if (fileAppender == null) {
            return false;
        }

        String originalFile = fileAppender.getFile();
        if (originalFile == null || originalFile.isBlank()) {
            return false;
        }

        Path currentLogPath = Path.of(originalFile).toAbsolutePath().normalize();

        return targetPaths.stream()
                .map(path -> path.toAbsolutePath().normalize())
                .anyMatch(currentLogPath::equals);
    }

    @Override
    public OperationResult executeWithTemporaryOutput(Supplier<OperationResult> operation) {
        FileAppender<ILoggingEvent> fileAppender = getFileAppender();
        if (fileAppender == null) {
            return OperationResult.failure("FILE appender が見つかりません。");
        }

        String originalFile = fileAppender.getFile();
        if (originalFile == null || originalFile.isBlank()) {
            return OperationResult.failure("FILE appender の出力先ファイルが取得できません。");
        }

        Path originalLogPath = Path.of(originalFile);
        Path temporaryLogPath = Path.of(originalFile + TEMPORARY_LOG_SUFFIX);

        synchronized (this) {
            if (Files.exists(temporaryLogPath)) {
                return OperationResult.failure("一時ログファイルが既に存在します: " + temporaryLogPath);
            }

            try {
                switchAppenderFile(fileAppender, temporaryLogPath);

                OperationResult result = operation.get();

                restoreAppenderFile(fileAppender, originalLogPath);
                mergeTemporaryLog(originalLogPath, temporaryLogPath);
                deleteTemporaryLog(temporaryLogPath);

                return result;
            } catch (Exception e) {
                restoreQuietly(fileAppender, originalLogPath);
                return OperationResult.failure("ログ出力先の一時切り替え処理に失敗しました: " + e.getMessage());
            }
        }
    }

    private FileAppender<ILoggingEvent> getFileAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        return (FileAppender<ILoggingEvent>) rootLogger.getAppender(FILE_APPENDER_NAME);
    }

    private void switchAppenderFile(FileAppender<ILoggingEvent> fileAppender, Path temporaryLogPath) {
        fileAppender.stop();
        fileAppender.setFile(temporaryLogPath.toString());
        fileAppender.start();
    }

    private void restoreAppenderFile(FileAppender<ILoggingEvent> fileAppender, Path originalLogPath) {
        fileAppender.stop();
        fileAppender.setFile(originalLogPath.toString());
        fileAppender.start();
    }

    private void restoreQuietly(FileAppender<ILoggingEvent> fileAppender, Path originalLogPath) {
        try {
            restoreAppenderFile(fileAppender, originalLogPath);
        } catch (Exception e) {
            // ログ出力先復旧中の例外は握り潰す。
        }
    }

    private void mergeTemporaryLog(Path originalLogPath, Path temporaryLogPath) throws IOException {
        if (!Files.exists(temporaryLogPath)) {
            return;
        }

        byte[] temporaryLogContent = Files.readAllBytes(temporaryLogPath);
        Files.write(originalLogPath, temporaryLogContent, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void deleteTemporaryLog(Path temporaryLogPath) throws IOException {
        Files.deleteIfExists(temporaryLogPath);
    }
}
