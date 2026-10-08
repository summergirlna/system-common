package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterStarter;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "system-common.cluster", name = "type", havingValue = "ha-monitor")
public class HaMonitorClusterStarter implements ClusterStarter {

    private final HaMonitorProperties properties;

    private final HaMonitorCommandExecutor executor;

    @Override
    public OperationResult start() {
        log.info("HAモニタのクラスタ起動処理を開始します。");

        OperationResult result = executor.execute("サーバ起動", properties.start());
        if (result.isFailure()) {
            return result;
        }

        log.info("HAモニタのクラスタ起動処理が正常終了しました。");
        return OperationResult.success("HAモニタのクラスタ起動処理が正常に終了しました。");
    }
}
