package com.example.systemcommon.middleware.script;

import com.example.systemcommon.middleware.MiddlewareProperties;
import com.example.systemcommon.middleware.MiddlewareStarter;
import com.example.systemcommon.operation.OperationResult;
import com.example.systemcommon.script.ScriptOperationExecutor;
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
        return executor.execute("ミドルウェア", "起動", properties.start().steps());
    }
}
