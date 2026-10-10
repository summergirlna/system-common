package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.online.OnlineStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnlineStartJob extends AbstractOperationJob {

    private final OnlineStarter onlineStarter;

    @Override
    public String name() {
        return "online-start";
    }

    @Override
    protected String operationName() {
        return "オンライン業務開始";
    }

    @Override
    protected OperationResult executeOperation() {
        return onlineStarter.start();
    }
}
