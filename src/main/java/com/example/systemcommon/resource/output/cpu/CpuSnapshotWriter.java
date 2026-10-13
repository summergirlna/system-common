package com.example.systemcommon.resource.output.cpu;

import com.example.systemcommon.resource.collect.cpu.CpuSnapshot;

public interface CpuSnapshotWriter {

    void write(CpuSnapshot snapshot);
}
