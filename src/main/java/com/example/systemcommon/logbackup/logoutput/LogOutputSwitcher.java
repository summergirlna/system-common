package com.example.systemcommon.logbackup.logoutput;

import com.example.systemcommon.fw.operation.OperationResult;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public interface LogOutputSwitcher {
    boolean isSwitchRequired(List<Path> targetPaths);

    OperationResult executeWithTemporaryOutput(Supplier<OperationResult> operation);
}
