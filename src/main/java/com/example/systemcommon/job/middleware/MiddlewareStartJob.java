package com.example.systemcommon.job.middleware;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.job.AbstractOperationJob;
import com.example.systemcommon.middleware.MiddlewareStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MiddlewareStartJob extends AbstractOperationJob {

    private final MiddlewareStarter middlewareStarter;

    @Override
    public String name() {
        return "middleware-start";
    }

    @Override
    protected String operationName() {
        return "ミドルウェア起動";
    }

    @Override
    protected OperationResult executeOperation() {
        return middlewareStarter.start();
    }
}
