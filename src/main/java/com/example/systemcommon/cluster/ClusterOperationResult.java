package com.example.systemcommon.cluster;

public record ClusterOperationResult(boolean success, String message) {

    public static ClusterOperationResult success(String message) {
        return new ClusterOperationResult(true, message);
    }

    public static ClusterOperationResult failure(String message) {
        return new ClusterOperationResult(false, message);
    }

    public boolean isFailure() {
        return !success;
    }
}
