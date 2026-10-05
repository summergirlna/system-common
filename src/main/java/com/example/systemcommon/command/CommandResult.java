package com.example.systemcommon.command;

import java.time.Duration;
import java.util.List;

public record CommandResult(
        List<String> command,
        int exitCode,
        String stdout,
        String stderr,
        Duration elapsed
) {
    public boolean isSuccess() {
        return exitCode == 0;
    }

    public boolean isFailure() {
        return !isSuccess();
    }
}
