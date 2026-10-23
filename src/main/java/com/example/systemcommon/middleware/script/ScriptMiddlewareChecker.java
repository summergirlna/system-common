package com.example.systemcommon.middleware.script;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.fw.script.ScriptOperationNames;
import com.example.systemcommon.middleware.MiddlewareChecker;
import com.example.systemcommon.middleware.MiddlewareProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptMiddlewareChecker implements MiddlewareChecker {

    private final MiddlewareProperties properties;

    private final ScriptOperationExecutor executor;

    @Override
    public OperationResult check() {
        return executor.execute(
                ScriptMiddlewareTargets.TARGET_NAME,
                ScriptOperationNames.CHECK,
                properties.check().steps());
    }
}
