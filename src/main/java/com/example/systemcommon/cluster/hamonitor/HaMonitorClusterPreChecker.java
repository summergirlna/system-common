package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterOperationResult;
import com.example.systemcommon.cluster.ClusterPreChecker;
import com.example.systemcommon.command.CommandExecutor;
import com.example.systemcommon.command.CommandResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "system-common.cluster", name = "type", havingValue = "ha-monitor")
public class HaMonitorClusterPreChecker extends AbstractHaMonitorClusterOperation implements ClusterPreChecker {

    public HaMonitorClusterPreChecker(CommandExecutor commandExecutor, HaMonitorProperties properties) {
        super(commandExecutor, properties);
    }

    @Override
    public ClusterOperationResult check() {
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
        return ClusterOperationResult.success("HAモニタのクラスタ起動前チェック処理が正常に終了しました。");
    }

    private CommandResult checkMonitorPath() {
        log.info("監視パスの状態チェックを開始します。");
        return execute(properties.monitorPath());
    }

    private CommandResult checkResetPath() {
        log.info("リセットパスの状態チェックを開始します。");
        return execute(properties.resetPath());
    }
}
