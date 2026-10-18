package com.example.systemcommon.resource.monitor;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.cpu.snapshot.CpuResourceSnapshotService;
import com.example.systemcommon.resource.memory.snapshot.MemoryResourceSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceMonitorRunner {

    private final ResourceMonitorProperties properties;

    private final CpuResourceSnapshotService cpuResourceSnapshotService;

    private final MemoryResourceSnapshotService memoryResourceSnapshotService;

    public OperationResult run() {
        log.info("リソース情報の定期取得を開始します。interval={}", properties.interval());

        while (!Thread.currentThread().isInterrupted()) {
            snapshotCpu();
            snapshotMemory();

            if (!sleep()) {
                break;
            }
        }

        log.info("リソース情報の定期取得を終了します。");
        return OperationResult.success("リソース情報の定期取得を終了しました。");
    }

    private void snapshotCpu() {
        OperationResult result = cpuResourceSnapshotService.snapshot();
        if (result.isFailure()) {
            log.error("CPUリソース情報の取得に失敗しました。message={}", result.message());
        }
    }

    private void snapshotMemory() {
        OperationResult result = memoryResourceSnapshotService.snapshot();
        if (result.isFailure()) {
            log.error("メモリリソース情報の取得に失敗しました。message={}", result.message());
        }
    }

    private boolean sleep() {
        try {
            Thread.sleep(properties.interval().toMillis());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("リソース情報の定期取得が中断されました。");
            return false;
        }
    }
}
