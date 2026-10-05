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
public class ScriptMiddlewareStarter implements MiddlewareStarter {

    private final CommandExecutor commandExecutor;

    private final MiddlewareStartProperties properties;

    @Override
    public OperationResult start() {
        log.info("ミドルウェア起動処理を開始します。");

        for (MiddlewareProductProperties product : properties.products()) {
            OperationResult result = startProduct(product);
            if (result.isFailure()) {
                return result;
            }
        }

        log.info("ミドルウェア起動処理が正常終了しました。");
        return OperationResult.success("ミドルウェア起動処理が正常に終了しました。");
    }

    private OperationResult startProduct(MiddlewareProductProperties product) {
        List<String> command = buildCommand(product);

        log.info("ミドルウェア起動を開始します。name={}, command={}", product.name(), command);

        CommandResult result = commandExecutor.execute(command, product.timeout());
        if (!product.successExitCodes().contains(result.exitCode())) {
            log.error(
                    "ミドルウェア起動に失敗しました。name={}, exitCode={}, stdout={}, stderr={}",
                    product.name(),
                    result.exitCode(),
                    result.stdout(),
                    result.stderr());
            return OperationResult.failure(product.name() + " の起動に失敗しました。exitCode=" + result.exitCode());
        }

        log.info("ミドルウェア起動が正常終了しました。name={}", product.name());
        return OperationResult.success(product.name() + " の起動が正常に終了しました。");
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
