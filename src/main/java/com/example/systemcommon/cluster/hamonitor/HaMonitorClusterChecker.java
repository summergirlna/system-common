package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterChecker;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "system-common.cluster", name = "type", havingValue = "ha-monitor")
public class HaMonitorClusterChecker implements ClusterChecker {

    private final HaMonitorProperties properties;

    private final HaMonitorCommandExecutor executor;

    @Override
    public OperationResult check() {
        log.info("HAモニタのクラスタ起動確認処理を開始します。");

        OperationResult result = executor.execute("クラスタ状態確認", properties.status());
        if (result.isFailure()) {
            return result;
        }

        log.info("HAモニタのクラスタ起動確認処理が正常終了しました。");
        return OperationResult.success("HAモニタのクラスタ起動確認処理が正常に終了しました。");
    }
}
