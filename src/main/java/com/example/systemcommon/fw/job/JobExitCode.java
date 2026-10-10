package com.example.systemcommon.fw.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobExitCode {
    SUCCESS(0),
    WARNING(10),
    BUSINESS_ERROR(20),
    SYSTEM_ERROR(30),
    INVALID_ARGUMENT(40),
    TIMEOUT(50),
    UNEXPECTED_ERROR(99);

    private final int code;
}
