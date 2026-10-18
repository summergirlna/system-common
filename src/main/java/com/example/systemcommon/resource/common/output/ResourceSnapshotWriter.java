package com.example.systemcommon.resource.common.output;

import com.example.systemcommon.resource.common.snapshot.ResourceSnapshot;

public interface ResourceSnapshotWriter<T extends ResourceSnapshot> {
    void write(T snapshot);
}
