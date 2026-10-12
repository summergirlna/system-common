package com.example.systemcommon.logbackup.logoutput.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.logoutput.LogOutputSwitcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogbackLogOutputSwitcher implements LogOutputSwitcher {

    private static final String AUDIT_LOGGER_NAME = "AUDIT";

    private static final String FILE_APPENDER_NAME = "FILE";

    private static final String AUDIT_FILE_APPENDER_NAME = "AUDIT_FILE";

    private static final String TEMPORARY_LOG_SUFFIX = ".archive-work";

    @Override
    public OperationResult executeWithTemporaryOutput(List<Path> targetPaths, Supplier<OperationResult> operation) {
        List<SwitchTarget> switchTargets = resolveSwitchTargets(targetPaths);

        if (switchTargets.isEmpty()) {
            return operation.get();
        }

        synchronized (this) {
            OperationResult validationResult = validateTemporaryFiles(switchTargets);
            if (validationResult.isFailure()) {
                return validationResult;
            }

            try {
                switchToTemporaryFiles(switchTargets);

                OperationResult result = operation.get();

                restoreAndMergeTemporaryFiles(switchTargets);

                return result;
            } catch (Exception e) {
                restoreQuietly(switchTargets);
                return OperationResult.failure("ログ出力先の一時切り替え処理に失敗しました: " + e.getMessage());
            }
        }
    }

    private List<SwitchTarget> resolveSwitchTargets(List<Path> targetPaths) {
        return configuredFileAppenders().stream()
                .filter(Objects::nonNull)
                .filter(appender -> isTargetAppender(appender, targetPaths))
                .map(this::toSwitchTarget)
                .toList();
    }

    private List<FileAppender<ILoggingEvent>> configuredFileAppenders() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        return List.of(
                findFileAppender(context, Logger.ROOT_LOGGER_NAME, FILE_APPENDER_NAME),
                findFileAppender(context, AUDIT_LOGGER_NAME, AUDIT_FILE_APPENDER_NAME));
    }

    private FileAppender<ILoggingEvent> findFileAppender(
            LoggerContext context, String loggerName, String appenderName) {
        Appender<ILoggingEvent> appender = context.getLogger(loggerName).getAppender(appenderName);
        if (appender instanceof FileAppender<?> fileAppender) {
            return (FileAppender<ILoggingEvent>) fileAppender;
        }

        return null;
    }

    private boolean isTargetAppender(FileAppender<ILoggingEvent> fileAppender, List<Path> targetPaths) {
        String file = fileAppender.getFile();
        if (file == null || file.isBlank()) {
            return false;
        }

        Path appenderLogPath = Path.of(file).toAbsolutePath().normalize();

        return targetPaths.stream()
                .map(path -> path.toAbsolutePath().normalize())
                .anyMatch(appenderLogPath::equals);
    }

    private SwitchTarget toSwitchTarget(FileAppender<ILoggingEvent> fileAppender) {
        Path originalLogPath = Path.of(fileAppender.getFile());
        Path temporaryLogPath = Path.of(fileAppender.getFile() + TEMPORARY_LOG_SUFFIX);

        return new SwitchTarget(fileAppender, originalLogPath, temporaryLogPath);
    }

    private OperationResult validateTemporaryFiles(List<SwitchTarget> switchTargets) {
        for (SwitchTarget switchTarget : switchTargets) {
            if (Files.exists(switchTarget.temporaryLogPath())) {
                return OperationResult.failure("一時ログファイルが既に存在します: " + switchTarget.temporaryLogPath());
            }
        }

        return OperationResult.success("一時ログファイルを使用できます。");
    }

    private void switchToTemporaryFiles(List<SwitchTarget> switchTargets) {
        for (SwitchTarget switchTarget : switchTargets) {
            switchAppenderFile(switchTarget.fileAppender(), switchTarget.temporaryLogPath());
        }
    }

    private void restoreAndMergeTemporaryFiles(List<SwitchTarget> switchTargets) throws IOException {
        for (SwitchTarget switchTarget : switchTargets) {
            restoreAppenderFile(switchTarget.fileAppender(), switchTarget.originalLogPath());
            mergeTemporaryLog(switchTarget.originalLogPath(), switchTarget.temporaryLogPath());
            deleteTemporaryLog(switchTarget.temporaryLogPath());
        }
    }

    private void restoreQuietly(List<SwitchTarget> switchTargets) {
        for (SwitchTarget switchTarget : switchTargets) {
            try {
                restoreAppenderFile(switchTarget.fileAppender(), switchTarget.originalLogPath());
            } catch (Exception e) {
                // ログ出力先復旧中の例外は握り潰す。
            }
        }
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

    private record SwitchTarget(
            FileAppender<ILoggingEvent> fileAppender, Path originalLogPath, Path temporaryLogPath) {}
}
