package com.example.systemcommon.job.application;

import com.example.systemcommon.application.ApplicationStopper;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.job.AbstractOperationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationStopJob extends AbstractOperationJob {

    private final ApplicationStopper applicationStopper;

    @Override
    public String name() {
        return "application-stop";
    }

    @Override
    protected String operationName() {
        return "アプリケーション停止";
    }

    @Override
    protected OperationResult executeOperation() {
        return applicationStopper.stop();
    }
}
