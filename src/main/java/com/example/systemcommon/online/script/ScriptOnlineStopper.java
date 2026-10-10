package com.example.systemcommon.online.script;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.online.OnlineProperties;
import com.example.systemcommon.online.OnlineStopper;
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
