package com.example.systemcommon.job.middleware;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.job.AbstractOperationJob;
import com.example.systemcommon.middleware.MiddlewareStopper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MiddlewareStopJob extends AbstractOperationJob {

    private final MiddlewareStopper middlewareStopper;

    @Override
    public String name() {
        return "middleware-stop";
    }

    @Override
    protected String operationName() {
        return "ミドルウェア停止";
    }

    @Override
    protected OperationResult executeOperation() {
        return middlewareStopper.stop();
    }
}
