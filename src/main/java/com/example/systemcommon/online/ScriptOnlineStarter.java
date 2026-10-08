package com.example.systemcommon.online;

import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptOnlineStarter implements OnlineStarter {

    private final OnlineProperties properties;

    private final ScriptOnlineOperationExecutor executor;

    @Override
    public OperationResult start() {
        return executor.execute("開始", properties.start().steps());
    }
}
