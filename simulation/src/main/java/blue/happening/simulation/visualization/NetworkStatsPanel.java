package blue.happening.simulation.visualization;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

class NetworkStatsPanel {

    private int timeWindow;
    private JPanel chartPanel;
    private XYChart chart;

    private List<Double> inValues;
    private List<Double> outValues;
    private List<Double> timeValues;

    NetworkStatsPanel(int windowSize) {

        timeWindow = windowSize;
        inValues = initCollection();
        outValues = initCollection();
        timeValues = initTimeAxis();

        chart = new XYChartBuilder().theme(Styler.ChartTheme.Matlab).build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setXAxisMax((double) timeWindow - 1);
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.getStyler().setChartBackgroundColor(new Color(229, 229, 229, 100));

        XYSeries in = chart.addSeries("Incoming", timeValues, inValues);
        XYSeries axis_1 = chart.addSeries("Axis_1", timeValues, initCollection());
        XYSeries out = chart.addSeries("Outgoing", timeValues, invertList(outValues));
        XYSeries axis_2 = chart.addSeries("Axis_2", timeValues, initCollection());

        in.setMarker(SeriesMarkers.NONE);
        in.setLineColor(Color.BLUE);

        out.setMarker(SeriesMarkers.NONE);
        out.setFillColor(Color.WHITE);
        out.setLineColor(Color.RED);

        axis_1.setMarker(SeriesMarkers.NONE);
        axis_1.setFillColor(Color.RED);
        axis_1.setLineColor(Color.WHITE);
        axis_1.setLineWidth(0f);
        axis_1.setShowInLegend(false);

        axis_2.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        axis_2.setMarker(SeriesMarkers.NONE);
        axis_2.setLineColor(Color.BLACK);
        axis_2.setShowInLegend(false);

        chartPanel = new XChartPanel<>(chart);

    }

    private LinkedList<Double> initCollection() {
        Double[] arr = new Double[timeWindow];
        LinkedList<Double> res = new LinkedList<>(Arrays.asList(arr));
        Collections.fill(res, 0.0);
        return res;
    }

    private LinkedList<Double> initTimeAxis() {
        LinkedList<Double> timeAxis = new LinkedList<>();
        for (int i = 0; i < timeWindow; i++) {
            timeAxis.add((double) i);
        }
        return timeAxis;
    }

    private LinkedList<Double> invertList(List<Double> list) {
        LinkedList<Double> negativeOut = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            negativeOut.add(i, Math.abs(list.get(i)) * -1);
        }
        return negativeOut;
    }

    JPanel getChartPanel() {
        chartPanel.setOpaque(false);
        return chartPanel;
    }

    void addValues(double in, double out) {

        inValues.add(in);
        while (inValues.size() > timeWindow) {
            inValues.remove(0);
        }

        outValues.add(out * -1);
        while (outValues.size() > timeWindow) {
            outValues.remove(0);
        }

        chart.updateXYSeries("Incoming", timeValues, inValues, null);
        chart.updateXYSeries("Outgoing", timeValues, outValues, null);
        chart.updateXYSeries("Axis_1", timeValues, initCollection(), null);
        chart.updateXYSeries("Axis_2", timeValues, initCollection(), null);

        chartPanel.revalidate();
        chartPanel.repaint();
    }

    void clear() {
        inValues.clear();
        outValues.clear();
        inValues = initCollection();
        outValues = initCollection();
    }

}