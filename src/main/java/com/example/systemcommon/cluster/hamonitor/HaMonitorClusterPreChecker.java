package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterPreCheckResult;
import com.example.systemcommon.cluster.ClusterPreChecker;
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
public class HaMonitorClusterPreChecker implements ClusterPreChecker {

    private final CommandExecutor commandExecutor;

    private final HaMonitorProperties properties;

    @Override
    public ClusterPreCheckResult check() {
        log.info("HAモニタのクラスタ起動前チェック処理を開始します。");

        CommandResult monitorPathResult = checkMonitorPath();
        if (isFailure(monitorPathResult, properties.monitorPath())) {
            return failure("監視パスの状態チェックに失敗しました。", monitorPathResult);
        }

        CommandResult resetPathResult = checkResetPath();
        if (isFailure(resetPathResult, properties.resetPath())) {
            return failure("リセットパスの状態チェックに失敗しました。", resetPathResult);
        }

        log.info("HAモニタのクラスタ起動前チェック処理が正常終了しました。");
        return ClusterPreCheckResult.success("HAモニタのクラスタ起動前チェック処理が正常に終了しました。");
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

    private boolean isFailure(CommandResult result, HaMonitorCommandProperties commandProperties) {
        return !commandProperties.successExitCodes().contains(result.exitCode());
    }

    private ClusterPreCheckResult failure(String message, CommandResult result) {
        log.error("{} exitCode={}, stdout={}, stderr={}", message, result.exitCode(), result.stdout(), result.stderr());
        return ClusterPreCheckResult.failure(message + " exitCode=" + result.exitCode());
    }
}
