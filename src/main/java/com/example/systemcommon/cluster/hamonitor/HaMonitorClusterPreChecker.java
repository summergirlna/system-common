package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterPreChecker;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "system-common.cluster", name = "type", havingValue = "ha-monitor")
public class HaMonitorClusterPreChecker implements ClusterPreChecker {

    private final HaMonitorProperties properties;

    private final HaMonitorCommandExecutor executor;

    @Override
    public OperationResult check() {
        log.info("HAモニタのクラスタ起動前チェック処理を開始します。");

        OperationResult monitorPathResult = executor.execute("監視パス状態チェック", properties.monitorPath());
        if (monitorPathResult.isFailure()) {
            return monitorPathResult;
        }

        OperationResult resetPathResult = executor.execute("リセットパス状態チェック", properties.resetPath());
        if (resetPathResult.isFailure()) {
            return resetPathResult;
        }

        log.info("HAモニタのクラスタ起動前チェック処理が正常終了しました。");
        return OperationResult.success("HAモニタのクラスタ起動前チェック処理が正常に終了しました。");
    }
}
