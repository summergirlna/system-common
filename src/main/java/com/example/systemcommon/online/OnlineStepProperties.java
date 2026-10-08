package com.example.systemcommon.online;

import java.time.Duration;
import java.util.List;

public record OnlineStepProperties(
        String name, String script, List<String> args, Duration timeout, List<Integer> successExitCodes) {}
