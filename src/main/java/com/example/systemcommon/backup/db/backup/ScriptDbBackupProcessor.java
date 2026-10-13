package com.example.systemcommon.backup.db.backup;

import com.example.systemcommon.backup.db.DbBackupContext;
import com.example.systemcommon.backup.db.DbBackupProperties;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.fw.script.ScriptOperationProperties;
import com.example.systemcommon.fw.script.ScriptStepProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScriptDbBackupProcessor implements DbBackupProcessor {

    private final DbBackupProperties properties;

    private final ScriptOperationExecutor scriptOperationExecutor;

    @Override
    public OperationResult backup(DbBackupContext context) {
        return scriptOperationExecutor.execute(
                "DB", "バックアップ", resolve(context, properties.backup()).steps());
    }

    private ScriptOperationProperties resolve(DbBackupContext context, ScriptOperationProperties properties) {
        List<ScriptStepProperties> steps = properties.steps().stream()
                .map(step -> new ScriptStepProperties(
                        step.name(),
                        resolve(context, step.script()),
                        resolve(context, step.args()),
                        step.timeout(),
                        step.successExitCodes()))
                .toList();

        return new ScriptOperationProperties(steps);
    }

    private List<String> resolve(DbBackupContext context, List<String> values) {
        if (values == null) {
            return null;
        }

        return values.stream().map(value -> resolve(context, value)).toList();
    }

    private String resolve(DbBackupContext context, String value) {
        if (value == null) {
            return null;
        }

        return value.replace("{operationDate}", context.operationDate().toString())
                .replace("{backupRootDirectory}", context.backupRootDirectory().toString())
                .replace("{backupId}", context.backupId());
    }
}
