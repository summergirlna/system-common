package com.example.systemcommon.job;

import com.example.systemcommon.application.ApplicationStarter;
import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationStartJob extends AbstractOperationJob {

    private final ApplicationStarter applicationStarter;

    @Override
    public String name() {
        return "application-start";
    }

    @Override
    protected String operationName() {
        return "アプリケーション起動";
    }

    @Override
    protected OperationResult executeOperation() {
        return applicationStarter.start();
    }
}
