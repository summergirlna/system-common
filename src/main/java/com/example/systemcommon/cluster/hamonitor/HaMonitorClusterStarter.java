package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterStarter;
import com.example.systemcommon.command.CommandExecutor;
import com.example.systemcommon.command.CommandResult;
import com.example.systemcommon.operation.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "system-common.cluster", name = "type", havingValue = "ha-monitor")
public class HaMonitorClusterStarter extends AbstractHaMonitorClusterOperation implements ClusterStarter {

    public HaMonitorClusterStarter(CommandExecutor commandExecutor, HaMonitorProperties properties) {
        super(commandExecutor, properties);
    }

    @Override
    public OperationResult start() {
        log.info("HAモニタのクラスタ起動処理を開始します。");

        CommandResult startResult = startHaMonitor();
        if (isFailure(startResult, properties.start())) {
            return failure("HAモニタのサーバ起動に失敗しました。", startResult);
        }

        log.info("HAモニタのクラスタ起動処理が正常終了しました。");
        return OperationResult.success("HAモニタのクラスタ起動処理が正常に終了しました。");
    }

    private CommandResult startHaMonitor() {
        log.info("HAモニタのサーバ起動を開始します。");
        return execute(properties.start());
    }
}
