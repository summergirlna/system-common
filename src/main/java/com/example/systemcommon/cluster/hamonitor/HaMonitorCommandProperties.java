package com.example.systemcommon.cluster.hamonitor;

import java.time.Duration;
import java.util.List;

public record HaMonitorCommandProperties(List<String> command, Duration timeout, List<Integer> successExitCodes) {}
