package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterStopper;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "system-common.cluster", name = "type", havingValue = "ha-monitor")
public class HaMonitorClusterStopper implements ClusterStopper {

    private final HaMonitorProperties properties;

    private final HaMonitorCommandExecutor executor;

    @Override
    public OperationResult stop() {
        log.info("HAモニタのクラスタ停止処理を開始します。");

        OperationResult result = executor.execute("サーバ停止", properties.stop());
        if (result.isFailure()) {
            return result;
        }

        log.info("HAモニタのクラスタ停止処理が正常終了しました。");
        return OperationResult.success("HAモニタのクラスタ停止処理が正常に終了しました。");
    }
}
