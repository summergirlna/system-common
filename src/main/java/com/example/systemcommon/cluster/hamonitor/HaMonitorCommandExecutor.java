package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.command.CommandOperationExecutor;
import com.example.systemcommon.command.CommandOperationProperties;
import com.example.systemcommon.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HaMonitorCommandExecutor {
    private final CommandOperationExecutor executor;

    public OperationResult execute(String operationName, CommandOperationProperties properties) {
        return executor.execute("HAモニタ", operationName, properties);
    }
}
