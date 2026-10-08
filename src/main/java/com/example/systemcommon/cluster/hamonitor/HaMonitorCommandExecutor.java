package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.command.CommandExecutor;
import com.example.systemcommon.command.CommandResult;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HaMonitorCommandExecutor {
    private final CommandExecutor commandExecutor;

    public OperationResult execute(String operationName, HaMonitorCommandProperties commandProperties) {
        log.info("HAモニタ{}を開始します。command={}", operationName, commandProperties.command());

        CommandResult result = commandExecutor.execute(commandProperties.command(), commandProperties.timeout());

        if (!commandProperties.successExitCodes().contains(result.exitCode())) {
            log.error(
                    "HAモニタ{}に失敗しました。exitCode={}, stdout={}, stderr={}",
                    operationName,
                    result.exitCode(),
                    result.stdout(),
                    result.stderr());
            return OperationResult.failure("HAモニタ" + operationName + "に失敗しました。exitCode=" + result.exitCode());
        }

        log.info("HAモニタ{}が正常終了しました。", operationName);
        return OperationResult.success("HAモニタ" + operationName + "が正常に終了しました。");
    }
}
