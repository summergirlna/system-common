package com.example.systemcommon.middleware;

import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptMiddlewareStarter implements MiddlewareStarter {

    private final MiddlewareProperties properties;

    private final ScriptMiddlewareOperationExecutor executor;

    @Override
    public OperationResult start() {
        return executor.execute("起動", properties.start().products());
    }
}
