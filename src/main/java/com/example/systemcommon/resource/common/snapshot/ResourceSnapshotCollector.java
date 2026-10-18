package com.example.systemcommon.resource.common.snapshot;

public interface ResourceSnapshotCollector<T extends ResourceSnapshot> {
    T collect();
}
