package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterStartResult;
import com.example.systemcommon.cluster.ClusterStarter;
import com.example.systemcommon.command.CommandExecutor;
import com.example.systemcommon.command.CommandResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "system-common.cluster", name = "type", havingValue = "ha-monitor")
public class HaMonitorClusterStarter implements ClusterStarter {

    private final CommandExecutor commandExecutor;

    private final HaMonitorProperties properties;

    @Override
    public ClusterStartResult start() {
        log.info("HAモニタのクラスタ起動処理を開始します。");

        CommandResult monitorPathResult = checkMonitorPath();
        if (isFailure(monitorPathResult, properties.monitorPath())) {
            return failure("監視パスの状態チェックに失敗しました。", monitorPathResult);
        }

        CommandResult resetPathResult = checkResetPath();
        if (isFailure(resetPathResult, properties.monitorPath())) {
            return failure("リセットパスの状態チェックに失敗しました。", resetPathResult);
        }

        CommandResult startResult = startHaMonitor();
        if (isFailure(startResult, properties.start())) {
            return failure("HAモニタのサーバ起動に失敗しました。", startResult);
        }

        log.info("HAモニタのクラスタ起動処理が正常終了しました。");
        return ClusterStartResult.success("HAモニタのクラスタ起動処理が正常に終了しました。");
    }

    private CommandResult checkMonitorPath() {
        log.info("監視パスの状態チェックを開始します。");
        return commandExecutor.execute(
                properties.monitorPath().command(), properties.monitorPath().timeout());
    }

    private CommandResult checkResetPath() {
        log.info("リセットパスの状態チェックを開始します。");
        return commandExecutor.execute(
                properties.resetPath().command(), properties.resetPath().timeout());
    }

    private CommandResult startHaMonitor() {
        log.info("HAモニタのサーバ起動を開始します。");
        return commandExecutor.execute(
                properties.start().command(), properties.start().timeout());
    }

    private boolean isFailure(CommandResult result, HaMonitorCommandProperties commandProperties) {
        return !commandProperties.successExitCodes().contains(result.exitCode());
    }

    private ClusterStartResult failure(String message, CommandResult result) {
        log.error("{} exitCode={}, stdout={}, stderr={}", message, result.exitCode(), result.stdout(), result.stderr());
        return ClusterStartResult.failure(message + " exitCode=" + result.exitCode());
    }
}
