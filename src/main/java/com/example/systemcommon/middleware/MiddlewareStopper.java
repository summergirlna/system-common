package com.example.systemcommon.middleware;

import com.example.systemcommon.operation.OperationResult;

public interface MiddlewareStopper {
    OperationResult stop();
}
