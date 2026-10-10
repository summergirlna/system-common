package com.example.systemcommon.application;

import com.example.systemcommon.operation.OperationResult;
import com.example.systemcommon.script.ScriptOperationExecutor;
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
        return executor.execute("アプリケーション", "起動", properties.start().steps());
    }
}
