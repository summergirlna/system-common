package com.example.systemcommon.middleware;

import com.example.systemcommon.fw.operation.OperationResult;

public interface MiddlewareStopper {
    OperationResult stop();
}
