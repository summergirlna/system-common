package com.example.systemcommon.middleware;

import java.time.Duration;
import java.util.List;

public record MiddlewareProductProperties(
        String name, String script, List<String> args, Duration timeout, List<Integer> successExitCodes) {}
