package com.example.systemcommon.resource.cpu.snapshot;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.cpu.collect.CpuSnapshot;
import com.example.systemcommon.resource.cpu.collect.CpuSnapshotCollector;
import com.example.systemcommon.resource.cpu.output.CpuSnapshotWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CpuResourceSnapshotService {

    private final CpuSnapshotCollector cpuSnapshotCollector;

    private final CpuSnapshotWriter cpuSnapshotWriter;

    public OperationResult snapshot() {
        log.debug("CPUリソース情報の取得を開始します。");

        try {
            CpuSnapshot snapshot = cpuSnapshotCollector.collect();
            cpuSnapshotWriter.write(snapshot);

            log.debug("CPUリソース情報の取得が完了しました。collectedAt={}", snapshot.collectedAt());
            return OperationResult.success("CPUリソース情報の取得が完了しました。");
        } catch (RuntimeException e) {
            log.error("CPUリソース情報の取得に失敗しました。", e);
            return OperationResult.failure("CPUリソース情報の取得に失敗しました。");
        }
    }
}
