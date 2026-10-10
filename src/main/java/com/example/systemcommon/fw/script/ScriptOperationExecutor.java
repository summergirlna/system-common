package com.example.systemcommon.fw.script;

import com.example.systemcommon.fw.command.CommandOperationExecutor;
import com.example.systemcommon.fw.command.CommandOperationProperties;
import com.example.systemcommon.fw.operation.OperationResult;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptOperationExecutor {
    private final CommandOperationExecutor commandOperationExecutor;

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
        CommandOperationProperties properties =
                new CommandOperationProperties(buildCommand(step), step.timeout(), step.successExitCodes());

        return commandOperationExecutor.execute(targetName, operationName + ":" + step.name(), properties);
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
