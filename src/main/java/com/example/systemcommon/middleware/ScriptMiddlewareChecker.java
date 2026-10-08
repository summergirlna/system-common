package com.example.systemcommon.middleware;

import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptMiddlewareChecker implements MiddlewareChecker {

    private final MiddlewareProperties properties;

    private final ScriptMiddlewareOperationExecutor executor;

    @Override
    public OperationResult check() {
        return executor.execute("チェック", properties.check().products());
    }
}
