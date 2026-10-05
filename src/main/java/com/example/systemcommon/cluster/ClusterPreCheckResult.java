package com.example.systemcommon.cluster;

public record ClusterPreCheckResult(boolean success, String message) {

    public static ClusterPreCheckResult success(String message) {
        return new ClusterPreCheckResult(true, message);
    }

    public static ClusterPreCheckResult failure(String message) {
        return new ClusterPreCheckResult(false, message);
    }

    public boolean isFailure() {
        return !success;
    }
}
