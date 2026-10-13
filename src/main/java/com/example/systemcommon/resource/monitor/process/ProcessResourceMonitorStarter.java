package com.example.systemcommon.resource.monitor.process;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.ResourceMonitorProperties;
import com.example.systemcommon.resource.monitor.ResourceMonitorStarter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessResourceMonitorStarter implements ResourceMonitorStarter {

    private static final String RESOURCE_MONITOR_RUN_JOB_NAME = "resource-monitor-run";

    private final ResourceMonitorProperties properties;

    @Override
    public OperationResult start() {
        log.info("リソース情報定期取得プロセスを起動します。runScript={}, pidFile={}", properties.runScript(), properties.pidFile());

        Path pidFile = properties.pidFile();
        if (Files.exists(pidFile)) {
            log.error("リソース情報定期取得プロセスのPIDファイルが既に存在します。pidFile={}", pidFile);
            return OperationResult.failure("リソース情報定期取得プロセスのPIDファイルが既に存在します: " + pidFile);
        }

        Path runScript = properties.runScript().toAbsolutePath().normalize();
        if (Files.notExists(runScript)) {
            log.error("リソース情報定期取得起動スクリプトが存在しません。runScript={}", runScript);
            return OperationResult.failure("リソース情報定期取得起動スクリプトが存在しません: " + runScript);
        }

        if (!Files.isRegularFile(runScript) || !Files.isExecutable(runScript)) {
            log.error("リソース情報定期取得起動スクリプトが実行可能ファイルではありません。runScript={}", runScript);
            return OperationResult.failure("リソース情報定期取得起動スクリプトが実行可能ファイルではありません: " + runScript);
        }

        try {
            Path pidFileParent = pidFile.getParent();
            if (pidFileParent != null) {
                Files.createDirectories(pidFileParent);
            }

            Process process = new ProcessBuilder(runScript.toString(), RESOURCE_MONITOR_RUN_JOB_NAME)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();

            long pid = process.pid();
            Files.writeString(pidFile, String.valueOf(pid), StandardCharsets.UTF_8);

            log.info("リソース情報定期取得プロセスを起動しました。pid={}, pidFile={}", pid, pidFile);
            return OperationResult.success("リソース情報定期取得プロセスを起動しました。pid=" + pid);

        } catch (IOException e) {
            log.error("リソース情報定期取得プロセスの起動に失敗しました。runScript={}, pidFile={}", properties.runScript(), pidFile, e);
            return OperationResult.failure("リソース情報定期取得プロセスの起動に失敗しました。");
        }
    }
}
