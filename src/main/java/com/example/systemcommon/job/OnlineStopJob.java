package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.online.OnlineStopper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnlineStopJob extends AbstractOperationJob {

    private final OnlineStopper onlineStopper;

    @Override
    public String name() {
        return "online-stop";
    }

    @Override
    protected String operationName() {
        return "オンライン業務停止";
    }

    @Override
    protected OperationResult executeOperation() {
        return onlineStopper.stop();
    }
}
