package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogBackupJob extends AbstractOperationJob {

    private final LogBackupExecutor logBackupExecutor;

    @Override
    protected String operationName() {
        return "ログ退避";
    }

    @Override
    protected OperationResult executeOperation() {
        return logBackupExecutor.backup();
    }

    @Override
    public String name() {
        return "log-backup";
    }
}
