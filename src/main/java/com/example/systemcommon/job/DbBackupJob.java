package com.example.systemcommon.job;

import com.example.systemcommon.backup.db.DbBackupService;
import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbBackupJob extends AbstractOperationJob {

    private final DbBackupService dbBackupService;

    @Override
    protected String operationName() {
        return "DBバックアップ";
    }

    @Override
    protected OperationResult executeOperation() {
        return dbBackupService.backup();
    }

    @Override
    public String name() {
        return "db-backup";
    }
}
