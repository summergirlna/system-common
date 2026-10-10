package com.example.systemcommon.fw.command;

import java.time.Duration;
import java.util.List;

public record CommandOperationProperties(List<String> command, Duration timeout, List<Integer> successExitCodes) {}
