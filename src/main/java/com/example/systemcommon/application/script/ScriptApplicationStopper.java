package com.example.systemcommon.application.script;

import com.example.systemcommon.application.ApplicationProperties;
import com.example.systemcommon.application.ApplicationStopper;
import com.example.systemcommon.operation.OperationResult;
import com.example.systemcommon.script.ScriptOperationExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptApplicationStopper implements ApplicationStopper {

    private final ApplicationProperties properties;

    private final ScriptOperationExecutor executor;

    @Override
    public OperationResult stop() {
        return executor.execute("アプリケーション", "停止", properties.stop().steps());
    }
}
