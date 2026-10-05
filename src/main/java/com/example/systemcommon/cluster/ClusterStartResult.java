package com.example.systemcommon.cluster;

public record ClusterStartResult(boolean success, String message) {
    public static ClusterStartResult success(String message) {
        return new ClusterStartResult(true, message);
    }

    public static ClusterStartResult failure(String message) {
        return new ClusterStartResult(false, message);
    }

    public boolean isFailure() {
        return !success;
    }
}
