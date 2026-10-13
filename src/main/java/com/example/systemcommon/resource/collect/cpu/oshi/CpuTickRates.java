package com.example.systemcommon.resource.collect.cpu.oshi;

public record CpuTickRates(double userRate, double systemRate, double idleRate, double ioWaitRate) {}
