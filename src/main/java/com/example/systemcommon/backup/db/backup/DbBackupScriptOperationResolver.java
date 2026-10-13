package com.example.systemcommon.backup.db.backup;

import com.example.systemcommon.backup.db.DbBackupContext;
import com.example.systemcommon.fw.script.ScriptOperationProperties;
import com.example.systemcommon.fw.script.ScriptStepProperties;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DbBackupScriptOperationResolver {

    public ScriptOperationProperties resolve(DbBackupContext context, ScriptOperationProperties properties) {
        if (properties == null) {
            return new ScriptOperationProperties(List.of());
        }

        if (properties.steps() == null) {
            return new ScriptOperationProperties(List.of());
        }

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
