package com.example.systemcommon.resource.monitor.process;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.monitor.ResourceMonitorProperties;
import com.example.systemcommon.resource.monitor.ResourceMonitorStopper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessResourceMonitorStopper implements ResourceMonitorStopper {

    private final ResourceMonitorProperties properties;

    @Override
    public OperationResult stop() {
        log.info("リソース情報定期取得プロセスを停止します。pidFile={}", properties.pidFile());

        Path pidFile = properties.pidFile();
        if (Files.notExists(pidFile)) {
            log.error("リソース情報定期取得プロセスのPIDファイルが存在しません。pidFile={}", pidFile);
            return OperationResult.failure("リソース情報定期取得プロセスのPIDファイルが存在しません: " + pidFile);
        }

        try {
            long pid = readPid(pidFile);
            Optional<ProcessHandle> processHandle = ProcessHandle.of(pid);

            if (processHandle.isEmpty() || !processHandle.get().isAlive()) {
                Files.deleteIfExists(pidFile);
                log.info("リソース情報定期取得プロセスは既に停止しています。pid={}", pid);
                return OperationResult.success("リソース情報定期取得プロセスは既に停止しています。pid=" + pid);
            }

            ProcessHandle handle = processHandle.get();
            handle.destroy();

            boolean stopped = handle.onExit()
                            .completeOnTimeout(null, properties.stopTimeout().toMillis(), TimeUnit.MILLISECONDS)
                            .join()
                    != null;

            if (!stopped && handle.isAlive()) {
                log.warn("リソース情報定期取得プロセスが停止しないため強制停止します。pid={}", pid);
                handle.destroyForcibly();
                handle.onExit().join();
            }

            Files.deleteIfExists(pidFile);

            log.info("リソース情報定期取得プロセスを停止しました。pid={}", pid);
            return OperationResult.success("リソース情報定期取得プロセスを停止しました。pid=" + pid);

        } catch (IOException | NumberFormatException e) {
            log.error("リソース情報定期取得プロセスの停止に失敗しました。pidFile={}", pidFile, e);
            return OperationResult.failure("リソース情報定期取得プロセスの停止に失敗しました。");
        }
    }

    private long readPid(Path pidFile) throws IOException {
        String value = Files.readString(pidFile, StandardCharsets.UTF_8).trim();
        return Long.parseLong(value);
    }
}
