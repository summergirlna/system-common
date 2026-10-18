package com.example.systemcommon.resource.memory.snapshot;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.memory.collect.MemorySnapshot;
import com.example.systemcommon.resource.memory.collect.MemorySnapshotCollector;
import com.example.systemcommon.resource.memory.output.MemorySnapshotWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemoryResourceSnapshotService {

    private final MemorySnapshotCollector memorySnapshotCollector;

    private final MemorySnapshotWriter memorySnapshotWriter;

    public OperationResult snapshot() {
        log.debug("メモリリソース情報の取得を開始します。");

        try {
            MemorySnapshot snapshot = memorySnapshotCollector.collect();
            memorySnapshotWriter.write(snapshot);

            log.debug("メモリリソース情報の取得が完了しました。collectedAt={}", snapshot.collectedAt());
            return OperationResult.success("メモリリソース情報の取得が完了しました。");
        } catch (RuntimeException e) {
            log.error("メモリリソース情報の取得に失敗しました。", e);
            return OperationResult.failure("メモリリソース情報の取得に失敗しました。");
        }
    }
}
