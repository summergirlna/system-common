package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.middleware.MiddlewareChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MiddlewareCheckJob extends AbstractOperationJob {

    private final MiddlewareChecker middlewareChecker;

    @Override
    public String name() {
        return "middleware-check";
    }

    @Override
    protected String operationName() {
        return "ミドルウェアチェック";
    }

    @Override
    protected OperationResult executeOperation() {
        return middlewareChecker.check();
    }
}
