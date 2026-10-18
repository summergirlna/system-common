package com.example.systemcommon.resource.cpu.collect.oshi;

import com.example.systemcommon.resource.cpu.collect.CpuSnapshot;
import com.example.systemcommon.resource.cpu.collect.CpuSnapshotCollector;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@Component
@Slf4j
public class OshiCpuSnapshotCollector implements CpuSnapshotCollector {

    private final CentralProcessor processor;

    private long[] previousSystemCpuTicks;

    public OshiCpuSnapshotCollector() {
        SystemInfo systemInfo = new SystemInfo();
        this.processor = systemInfo.getHardware().getProcessor();
        this.previousSystemCpuTicks = processor.getSystemCpuLoadTicks();
    }

    @Override
    public CpuSnapshot collect() {
        long[] currentSystemCpuTicks = processor.getSystemCpuLoadTicks();

        // cpu情報の取得。 0.0~1.0
        double systemCpuLoad = processor.getSystemCpuLoadBetweenTicks(previousSystemCpuTicks);
        CpuTickRates cpuTickRates = calculateCpuTickRates(previousSystemCpuTicks, currentSystemCpuTicks);

        // OS や環境によっては-1.0で返される可能性あり。
        // これは取得不可を意味する値として、そのまま持たせる。
        double[] loadAverages = processor.getSystemLoadAverage(3);

        previousSystemCpuTicks = currentSystemCpuTicks;

        return new CpuSnapshot(
                Instant.now(),
                processor.getLogicalProcessorCount(),
                processor.getPhysicalProcessorCount(),
                normalize(systemCpuLoad),
                cpuTickRates.userRate(),
                cpuTickRates.systemRate(),
                cpuTickRates.idleRate(),
                cpuTickRates.ioWaitRate(),
                loadAverage(loadAverages, 0),
                loadAverage(loadAverages, 1),
                loadAverage(loadAverages, 2));
    }

    private CpuTickRates calculateCpuTickRates(long[] previousTicks, long[] currentTicks) {
        // CPU tickの差分から計算する。
        // 前回取得時点から今回取得時点までの間に、CPU時間の何割が user / system / idle / iowait だったか。
        long user = diff(previousTicks, currentTicks, CentralProcessor.TickType.USER);
        long nice = diff(previousTicks, currentTicks, CentralProcessor.TickType.NICE);
        long system = diff(previousTicks, currentTicks, CentralProcessor.TickType.SYSTEM);
        long idle = diff(previousTicks, currentTicks, CentralProcessor.TickType.IDLE);
        long ioWait = diff(previousTicks, currentTicks, CentralProcessor.TickType.IOWAIT);
        long irq = diff(previousTicks, currentTicks, CentralProcessor.TickType.IRQ);
        long softIrq = diff(previousTicks, currentTicks, CentralProcessor.TickType.SOFTIRQ);
        long steal = diff(previousTicks, currentTicks, CentralProcessor.TickType.STEAL);

        long total = user + nice + system + idle + ioWait + irq + softIrq + steal;
        if (total <= 0) {
            log.warn("CPU tick差分が0以下です。user={}, system={}, idle={}", user, system, idle);
            return new CpuTickRates(0.0, 0.0, 0.0, 0.0);
        }

        return new CpuTickRates(
                (double) (user + nice) / total,
                (double) (system + irq + softIrq) / total,
                (double) idle / total,
                (double) ioWait / total);
    }

    private long diff(long[] previousTicks, long[] currentTicks, CentralProcessor.TickType tickType) {
        return currentTicks[tickType.getIndex()] - previousTicks[tickType.getIndex()];
    }

    private double loadAverage(double[] loadAverages, int index) {
        if (loadAverages.length <= index) {
            return -1.0;
        }

        return loadAverages[index];
    }

    private double normalize(double value) {
        if (Double.isNaN(value) || value < 0.0) {
            return 0.0;
        }

        if (value > 1.0) {
            return 1.0;
        }

        return value;
    }
}
