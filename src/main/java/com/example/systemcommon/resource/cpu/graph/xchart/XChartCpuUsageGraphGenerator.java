package com.example.systemcommon.resource.cpu.graph.xchart;

import com.example.systemcommon.resource.cpu.graph.CpuUsageDataPoint;
import com.example.systemcommon.resource.cpu.graph.CpuUsageGraphGenerator;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class XChartCpuUsageGraphGenerator implements CpuUsageGraphGenerator {

    private static final int WIDTH = 1280;

    private static final int HEIGHT = 720;

    private static final String IMAGE_FORMAT = "png";

    public void generate(List<CpuUsageDataPoint> dataPoints, Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());

        XYChart chart = createChart(dataPoints);

        try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
            ChartEncoder.saveChart(chart, outputStream, IMAGE_FORMAT);
        }

        log.debug("XChartでCPU使用率グラフを出力しました。outputPath={}", outputPath);
    }

    private XYChart createChart(List<CpuUsageDataPoint> dataPoints) {
        XYChart chart = new XYChartBuilder()
                .width(WIDTH)
                .height(HEIGHT)
                .title("CPU Usage")
                .xAxisTitle("Time")
                .yAxisTitle("Usage (%)")
                .build();

        configureStyle(chart);

        List<Date> xData = toXData(dataPoints);

        addSeries(
                chart,
                "System CPU Load",
                xData,
                dataPoints.stream().map(CpuUsageDataPoint::systemCpuLoadPercent).toList(),
                new Color(31, 119, 180));

        addSeries(
                chart,
                "User",
                xData,
                dataPoints.stream().map(CpuUsageDataPoint::userRatePercent).toList(),
                new Color(44, 160, 44));

        addSeries(
                chart,
                "System",
                xData,
                dataPoints.stream().map(CpuUsageDataPoint::systemRatePercent).toList(),
                new Color(255, 127, 14));

        addSeries(
                chart,
                "I/O Wait",
                xData,
                dataPoints.stream().map(CpuUsageDataPoint::ioWaitRatePercent).toList(),
                new Color(214, 39, 40));

        return chart;
    }

    private void configureStyle(XYChart chart) {
        Styler styler = chart.getStyler();
        styler.setLegendPosition(Styler.LegendPosition.InsideNE);
        styler.setMarkerSize(0);
    }

    private List<Date> toXData(List<CpuUsageDataPoint> dataPoints) {
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
