package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterChecker;
import com.example.systemcommon.command.CommandExecutor;
import com.example.systemcommon.command.CommandResult;
import com.example.systemcommon.operation.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HaMonitorClusterChecker extends AbstractHaMonitorClusterOperation implements ClusterChecker {

    public HaMonitorClusterChecker(CommandExecutor commandExecutor, HaMonitorProperties properties) {
        super(commandExecutor, properties);
    }

    @Override
    public OperationResult check() {
        log.info("HAモニタのクラスタ起動確認処理を開始します。");

        CommandResult statusResult = checkStatus();
        if (isFailure(statusResult, properties.status())) {
            return failure("HAモニタのクラスタ起動確認処理に失敗しました。", statusResult);
        }

        log.info("HAモニタのクラスタ起動確認処理が正常終了しました。");
        return OperationResult.success("HAモニタのクラスタ起動確認処理が正常に終了しました。");
    }

    private CommandResult checkStatus() {
        log.info("HAモニタのクラスタ状態確認処理を開始します。");
        return execute(properties.status());
    }
}
