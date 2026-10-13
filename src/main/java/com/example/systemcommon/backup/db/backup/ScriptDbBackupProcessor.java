package com.example.systemcommon.backup.db.backup;

import com.example.systemcommon.backup.db.DbBackupContext;
import com.example.systemcommon.backup.db.DbBackupProperties;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScriptDbBackupProcessor implements DbBackupProcessor {

    private final DbBackupProperties properties;

    private final DbBackupScriptOperationResolver scriptOperationResolver;

    private final ScriptOperationExecutor scriptOperationExecutor;

    @Override
    public OperationResult backup(DbBackupContext context) {

        return scriptOperationExecutor.execute(
                "DB",
                "バックアップ",
                scriptOperationResolver.resolve(context, properties.backup()).steps());
    }
}
