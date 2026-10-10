package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.operationdate.OperationDateUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationDateUpdateJob extends AbstractOperationJob {

    private final OperationDateUpdater operationDateUpdater;

    @Override
    protected String operationName() {
        return "運用日付更新";
    }

    @Override
    protected OperationResult executeOperation() {
        return operationDateUpdater.update();
    }

    @Override
    public String name() {
        return "operation-date-update";
    }
}
