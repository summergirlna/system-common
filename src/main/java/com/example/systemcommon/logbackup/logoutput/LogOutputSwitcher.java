package com.example.systemcommon.logbackup.logoutput;

import com.example.systemcommon.fw.operation.OperationResult;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public interface LogOutputSwitcher {
    OperationResult executeWithTemporaryOutput(List<Path> targetPaths, Supplier<OperationResult> operation);
}
