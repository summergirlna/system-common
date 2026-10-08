package com.example.systemcommon.middleware;

import com.example.systemcommon.command.CommandExecutor;
import com.example.systemcommon.command.CommandResult;
import com.example.systemcommon.operation.OperationResult;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptMiddlewareOperationExecutor {

    private final CommandExecutor commandExecutor;

    public OperationResult execute(String operationName, List<MiddlewareProductProperties> products) {
        log.info("ミドルウェア{}処理を開始します。", operationName);

        for (MiddlewareProductProperties product : products) {
            OperationResult result = executeProduct(operationName, product);
            if (result.isFailure()) {
                return result;
            }
        }

        log.info("ミドルウェア{}処理が正常終了しました。", operationName);
        return OperationResult.success("ミドルウェア" + operationName + "処理が正常に終了しました。");
    }

    private OperationResult executeProduct(String operationName, MiddlewareProductProperties product) {
        List<String> command = buildCommand(product);

        log.info("ミドルウェア{}を開始します。name={}, command={}", operationName, product.name(), command);

        CommandResult result = commandExecutor.execute(command, product.timeout());
        if (!product.successExitCodes().contains(result.exitCode())) {
            log.error(
                    "ミドルウェア{}に失敗しました。name={}, exitCode={}, stdout={}, stderr={}",
                    operationName,
                    product.name(),
                    result.exitCode(),
                    result.stdout(),
                    result.stderr());
            return OperationResult.failure(
                    product.name() + " の" + operationName + "に失敗しました。exitCode=" + result.exitCode());
        }

        log.info("ミドルウェア{}が正常終了しました。name={}", operationName, product.name());
        return OperationResult.success(product.name() + " の" + operationName + "が正常に終了しました。");
    }

    private List<String> buildCommand(MiddlewareProductProperties product) {
        List<String> command = new ArrayList<>();
        command.add(product.script());

        if (product.args() != null) {
            command.addAll(product.args());
        }

        return command;
    }
}
