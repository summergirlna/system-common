package com.example.systemcommon.script;

import java.time.Duration;
import java.util.List;

public record ScriptStepProperties(
        String name, String script, List<String> args, Duration timeout, List<Integer> successExitCodes) {}
