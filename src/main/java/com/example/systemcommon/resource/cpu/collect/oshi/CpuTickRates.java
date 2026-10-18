package com.example.systemcommon.resource.cpu.collect.oshi;

public record CpuTickRates(double userRate, double systemRate, double idleRate, double ioWaitRate) {}
