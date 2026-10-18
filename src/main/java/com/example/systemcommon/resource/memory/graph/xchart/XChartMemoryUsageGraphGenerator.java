package com.example.systemcommon.resource.memory.graph.xchart;

import com.example.systemcommon.fw.file.ParentDirectoryCreator;
import com.example.systemcommon.resource.memory.graph.MemoryUsageDataPoint;
import com.example.systemcommon.resource.memory.graph.MemoryUsageGraphGenerator;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.ChartEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class XChartMemoryUsageGraphGenerator implements MemoryUsageGraphGenerator {

    private static final int WIDTH = 1280;

    private static final int HEIGHT = 720;

    private static final String IMAGE_FORMAT = "png";

    private final ParentDirectoryCreator parentDirectoryCreator;

    @Override
    public void generate(List<MemoryUsageDataPoint> dataPoints, Path outputPath) throws IOException {
        parentDirectoryCreator.createParentOf(outputPath);

        XYChart chart = createChart(dataPoints);

        try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
            ChartEncoder.saveChart(chart, outputStream, IMAGE_FORMAT);
        }

        log.debug("XChartでメモリ使用率グラフを出力しました。outputPath={}", outputPath);
    }

    private XYChart createChart(List<MemoryUsageDataPoint> dataPoints) {
        XYChart chart = new XYChartBuilder()
                .width(WIDTH)
                .height(HEIGHT)
                .title("Memory Usage")
                .xAxisTitle("Time")
                .yAxisTitle("Usage (%)")
                .build();

        configureStyle(chart);

        List<Date> xData = toXdata(dataPoints);

        addSeries(
                chart,
                "Memory",
                xData,
                dataPoints.stream()
                        .map(MemoryUsageDataPoint::usedMemoryRatePercent)
                        .toList(),
                new Color(31, 119, 180));

        addSeries(
                chart,
                "Swap",
                xData,
                dataPoints.stream()
                        .map(MemoryUsageDataPoint::usedSwapRatePercent)
                        .toList(),
                new Color(255, 127, 14));

        return chart;
    }

    private void configureStyle(XYChart chart) {
        Styler styler = chart.getStyler();
        styler.setLegendPosition(Styler.LegendPosition.InsideNE);
        styler.setMarkerSize(0);
    }

    private List<Date> toXdata(List<MemoryUsageDataPoint> dataPoints) {
        return dataPoints.stream()
                .map(dataPoint -> Date.from(dataPoint.collectedAt()))
                .toList();
    }

    private void addSeries(XYChart chart, String name, List<Date> xData, List<Double> yData, Color color) {
        XYSeries series = chart.addSeries(name, xData, yData);
        series.setLineColor(color);
        series.setMarkerColor(color);
    }
}
