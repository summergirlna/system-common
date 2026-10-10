package com.example.systemcommon.fw.command;

import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommandOperationExecutor {

    private final CommandExecutor commandExecutor;

    public OperationResult execute(String targetName, String operationName, CommandOperationProperties properties) {
        log.info("{}{}を開始します。command={}", targetName, operationName, properties.command());

        CommandResult result = commandExecutor.execute(properties.command(), properties.timeout());
        if (!properties.successExitCodes().contains(result.exitCode())) {
            log.error(
                    "{}{}に失敗しました。exitCode={}, stdout={}, stderr={}",
                    targetName,
                    operationName,
                    result.exitCode(),
                    result.stdout(),
                    result.stderr());
            return OperationResult.failure(targetName + operationName + "に失敗しました。exitCode=" + result.exitCode());
        }

        log.info("{}{}が正常終了しました。", targetName, operationName);
        return OperationResult.success(targetName + operationName + "が正常に終了しました。");
    }
}
