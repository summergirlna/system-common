package com.example.systemcommon.middleware.script;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.middleware.MiddlewareProperties;
import com.example.systemcommon.middleware.MiddlewareStopper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptMiddlewareStopper implements MiddlewareStopper {

    private final MiddlewareProperties properties;

    private final ScriptOperationExecutor executor;

    @Override
    public OperationResult stop() {
        return executor.execute("ミドルウェア", "停止", properties.stop().steps());
    }
}
