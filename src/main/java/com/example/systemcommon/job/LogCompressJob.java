package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.AbstractOperationJob;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.compress.LogCompressExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogCompressJob extends AbstractOperationJob {

    private final LogCompressExecutor logCompressExecutor;

    @Override
    protected String operationName() {
        return "ログ圧縮";
    }

    @Override
    protected OperationResult executeOperation() {
        return logCompressExecutor.compress();
    }

    @Override
    public String name() {
        return "log-compress";
    }
}
