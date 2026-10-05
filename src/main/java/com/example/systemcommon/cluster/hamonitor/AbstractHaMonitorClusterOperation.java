package com.example.systemcommon.cluster.hamonitor;

import com.example.systemcommon.cluster.ClusterOperationResult;
import com.example.systemcommon.command.CommandExecutor;
import com.example.systemcommon.command.CommandResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractHaMonitorClusterOperation {

    protected final CommandExecutor commandExecutor;

    protected final HaMonitorProperties properties;

    protected CommandResult execute(HaMonitorCommandProperties commandProperties) {
        return commandExecutor.execute(commandProperties.command(), commandProperties.timeout());
    }

    protected boolean isFailure(CommandResult result, HaMonitorCommandProperties commandProperties) {
        return !commandProperties.successExitCodes().contains(result.exitCode());
    }

    protected ClusterOperationResult failure(String message, CommandResult result) {
        log.error("{} exitCode={}, stdout={}, stderr={}", message, result.exitCode(), result.stdout(), result.stderr());
        return ClusterOperationResult.failure(message + " exitCode=" + result.exitCode());
    }
}
