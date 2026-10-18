package com.example.systemcommon.resource.cpu.output;

import com.example.systemcommon.resource.cpu.collect.CpuSnapshot;

public interface CpuSnapshotWriter {

    void write(CpuSnapshot snapshot);
}
