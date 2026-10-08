package com.example.systemcommon.online;

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
public class ScriptOnlineOperationExecutor {

    private final CommandExecutor commandExecutor;

    public OperationResult execute(String operationName, List<OnlineStepProperties> steps) {
        log.info("オンライン業務{}処理を開始します。", operationName);

        for (OnlineStepProperties step : steps) {
            OperationResult result = executeStep(operationName, step);
            if (result.isFailure()) {
                return result;
            }
        }

        log.info("オンライン業務{}処理が正常終了しました。", operationName);
        return OperationResult.success("オンライン業務" + operationName + "処理が正常に終了しました。");
    }

    private OperationResult executeStep(String operationName, OnlineStepProperties step) {
        List<String> command = buildCommand(step);

        log.info("オンライン業務{}を開始します。name={}, command={}", operationName, step.name(), command);

        CommandResult result = commandExecutor.execute(command, step.timeout());
        if (!step.successExitCodes().contains(result.exitCode())) {
            log.error(
                    "オンライン業務{}に失敗しました。name={}, exitCode={}, stdout={}, stderr={}",
                    operationName,
                    step.name(),
                    result.exitCode(),
                    result.stdout(),
                    result.stderr());
            return OperationResult.failure(
                    step.name() + " の" + operationName + "に失敗しました。exitCode=" + result.exitCode());
        }

        log.info("オンライン業務{}が正常終了しました。name={}", operationName, step.name());
        return OperationResult.success(step.name() + " の" + operationName + "が正常に終了しました。");
    }

    private List<String> buildCommand(OnlineStepProperties step) {
        List<String> command = new ArrayList<>();
        command.add(step.script());

        if (step.args() != null) {
            command.addAll(step.args());
        }

        return command;
    }
}
