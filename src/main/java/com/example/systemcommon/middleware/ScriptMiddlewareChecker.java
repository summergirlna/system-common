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
public class ScriptMiddlewareChecker implements MiddlewareChecker {

    private final CommandExecutor commandExecutor;

    private final MiddlewareCheckProperties properties;

    @Override
    public OperationResult check() {
        log.info("ミドルウェアチェック処理を開始します。");

        for (MiddlewareProductProperties product : properties.products()) {
            OperationResult result = checkProduct(product);
            if (result.isFailure()) {
                return result;
            }
        }

        log.info("ミドルウェアチェック処理が正常終了しました。");
        return OperationResult.success("ミドルウェアチェック処理が正常に終了しました。");
    }

    private OperationResult checkProduct(MiddlewareProductProperties product) {
        List<String> command = buildCommand(product);

        log.info("ミドルウェアチェックを開始します。name={}, command={}", product.name(), command);

        CommandResult result = commandExecutor.execute(command, product.timeout());
        if (!product.successExitCodes().contains(result.exitCode())) {
            log.error(
                    "ミドルウェアチェックに失敗しました。name={}, exitCode={}, stdout={}, stderr={}",
                    product.name(),
                    result.exitCode(),
                    result.stdout(),
                    result.stderr());
            return OperationResult.failure(product.name() + " のチェックに失敗しました。exitCode=" + result.exitCode());
        }

        log.info("ミドルウェアチェックが正常終了しました。name={}", product.name());
        return OperationResult.success(product.name() + " のチェックが正常終了しました。");
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
