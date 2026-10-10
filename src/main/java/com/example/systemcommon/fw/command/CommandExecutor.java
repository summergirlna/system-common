package com.example.systemcommon.fw.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommandExecutor {

    private static final Charset COMMAND_OUTPUT_CHARSET = Charset.defaultCharset();

    public CommandResult execute(List<String> command, Duration timeout) {
        validate(command, timeout);

        Instant start = Instant.now();

        Process process = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            process = processBuilder.start();

            boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);

            if (!finished) {
                process.destroyForcibly();

                Duration elapsed = Duration.between(start, Instant.now());

                String stdout = read(process.getInputStream());
                String stderr = read(process.getErrorStream());

                log.error("外部コマンドがタイムアウトしました: command={}, timeout={}, elapsed={}", command, timeout, elapsed);

                return new CommandResult(
                        command, -1, // タイムアウトは一律-1扱い
                        stdout, stderr, elapsed);
            }

            int exitCode = process.exitValue();
            String stdout = read(process.getInputStream());
            String stderr = read(process.getErrorStream());
            Duration elapsed = Duration.between(start, Instant.now());

            log.info("外部コマンド実行終了: command={}, exitCode={}, elapsed={}", command, exitCode, elapsed);

            if (!stdout.isBlank()) {
                log.info("外部コマンド標準出力: command={}, stdout={}", command, stdout);
            }

            if (!stderr.isBlank()) {
                log.info("外部コマンド標準エラー: command={}, stderr={}", command, stderr);
            }

            return new CommandResult(command, exitCode, stdout, stderr, elapsed);
        } catch (IOException e) {
            Duration elapsed = Duration.between(start, Instant.now());
            log.error("外部コマンド実行時にI/Oエラーが発生しました: command={}, elapsed={}", command, elapsed, e);
            throw new IllegalStateException("外部コマンド実行時にI/Oエラーが発生しました: " + command, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Duration elapsed = Duration.between(start, Instant.now());
            log.error("外部コマンド実行が中断されました: command={}, elapsed={}", command, elapsed, e);
            throw new IllegalStateException("外部コマンド実行が中断されました: " + command, e);
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private void validate(List<String> command, Duration timeout) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("command must not be empty.");
        }

        if (command.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalArgumentException("command must not contain blank value.");
        }

        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive.");
        }
    }

    private String read(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, COMMAND_OUTPUT_CHARSET))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new IllegalStateException("外部コマンドの出力読み取りに失敗しました。");
        }
    }
}
