package com.example.systemcommon.script;

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
public class ScriptOperationExecutor {
    private final CommandExecutor commandExecutor;

    public OperationResult execute(String targetName, String operationName, List<ScriptStepProperties> steps) {
        log.info("{}{}処理を開始します。", targetName, operationName);

        for (ScriptStepProperties step : steps) {
            OperationResult result = executeStep(targetName, operationName, step);
            if (result.isFailure()) {
                return result;
            }
        }

        log.info("{}{}処理が正常終了しました。", targetName, operationName);
        return OperationResult.success(targetName + operationName + "処理が正常に終了しました。");
    }

    private OperationResult executeStep(String targetName, String operationName, ScriptStepProperties step) {
        List<String> command = buildCommand(step);

        log.info("{}{}を開始します。name={}, command={}", targetName, operationName, step.name(), command);

        CommandResult result = commandExecutor.execute(command, step.timeout());
        if (!step.successExitCodes().contains(result.exitCode())) {
            log.error(
                    "{}{}に失敗しました。name={}, exitCode={}, stdout={}, stderr={}",
                    targetName,
                    operationName,
                    step.name(),
                    result.exitCode(),
                    result.stdout(),
                    result.stderr());
            return OperationResult.failure(
                    step.name() + " の" + operationName + "に失敗しました。exitCode=" + result.exitCode());
        }

        log.info("{}{}が正常終了しました。name={}", targetName, operationName, step.name());
        return OperationResult.success(step.name() + " の" + operationName + "が正常に終了しました。");
    }

    private List<String> buildCommand(ScriptStepProperties step) {
        List<String> command = new ArrayList<>();
        command.add(step.script());

        if (step.args() != null) {
            command.addAll(step.args());
        }

        return command;
    }
}
