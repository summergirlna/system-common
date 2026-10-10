package com.example.systemcommon.online;

import com.example.systemcommon.operation.OperationResult;
import com.example.systemcommon.script.ScriptOperationExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptOnlineStopper implements OnlineStopper {
    private final OnlineProperties properties;

    private final ScriptOperationExecutor executor;

    @Override
    public OperationResult stop() {
        return executor.execute("オンライン業務", "停止", properties.stop().steps());
    }
}
