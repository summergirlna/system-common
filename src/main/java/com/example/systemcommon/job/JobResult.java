package com.example.systemcommon.job;

public record JobResult(JobExitCode exitCode, String message) {
    public static JobResult success(String message) {
        return new JobResult(JobExitCode.SUCCESS, message);
    }

    public static JobResult failure(JobExitCode exitCode, String message) {
        return new JobResult(exitCode, message);
    }

    public int code() {
        return exitCode.getCode();
    }
}
