package com.example.systemcommon.application.script;

import com.example.systemcommon.application.ApplicationProperties;
import com.example.systemcommon.application.ApplicationStarter;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.fw.script.ScriptOperationNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptApplicationStarter implements ApplicationStarter {

    private final ApplicationProperties properties;

    private final ScriptOperationExecutor executor;

    @Override
    public OperationResult start() {
        return executor.execute(
                ScriptApplicationTargets.TARGET_NAME,
                ScriptOperationNames.START,
                properties.start().steps());
    }
}
