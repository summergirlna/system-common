package com.example.systemcommon.middleware.script;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.fw.script.ScriptOperationNames;
import com.example.systemcommon.middleware.MiddlewareProperties;
import com.example.systemcommon.middleware.MiddlewareStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptMiddlewareStarter implements MiddlewareStarter {

    private final MiddlewareProperties properties;

    private final ScriptOperationExecutor executor;

    @Override
    public OperationResult start() {
        return executor.execute(
                ScriptMiddlewareTargets.TARGET_NAME,
                ScriptOperationNames.START,
                properties.start().steps());
    }
}
