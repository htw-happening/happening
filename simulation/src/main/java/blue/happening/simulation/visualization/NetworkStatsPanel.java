package blue.happening.simulation.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

class NetworkStatsPanel extends JPanel {

    private int width = 800;
    private int heigth = 400;
    private int padding = 25;
    private int labelPadding = 25;
    private Color lineColor1 = new Color(44, 102, 230, 180);
    private Color lineColor2 = new Color(38, 204, 0, 219);
    private Color lineColor3 = new Color(204, 0, 9, 219);
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 10;
    private List<Double> totalValues;
    private List<Double> ogmValues;
    private List<Double> ucmValues;

    NetworkStatsPanel(List<Double> ogm, List<Double> ucm) {
        this.totalValues = new ArrayList<>();
        this.ogmValues = ogm;
        this.ucmValues = ucm;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (ogmValues.size() - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxValue() - getMinValue());

        List<Point> graphPoints1 = new ArrayList<>();
        List<Point> graphPoints2 = new ArrayList<>();
        List<Point> graphPoints3 = new ArrayList<>();

        for (int i = 0; i < ogmValues.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((getMaxValue() - ogmValues.get(i)) * yScale + padding);
            graphPoints1.add(new Point(x1, y1));

            int x2 = (int) (i * xScale + padding + labelPadding);
            int y2 = (int) ((getMaxValue() - ucmValues.get(i)) * yScale + padding);
            graphPoints2.add(new Point(x2, y2));

            totalValues.add(i, ogmValues.get(i) + ucmValues.get(i));
            int x3 = (int) (i * xScale + padding + labelPadding);
            int y3 = (int) ((getMaxValue() - totalValues.get(i)) * yScale + padding);
            graphPoints3.add(new Point(x3, y3));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (ogmValues.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinValue() + (getMaxValue() - getMinValue()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // and for x axis
        for (int i = 0; i < ogmValues.size(); i++) {
            if (ogmValues.size() > 1) {
                int x0 = i * (getWidth() - padding * 2 - labelPadding) / (ogmValues.size() - 1) + padding + labelPadding;
                int x1 = x0;
                int y0 = getHeight() - padding - labelPadding;
                int y1 = y0 - pointWidth;
                if ((i % ((int) ((ogmValues.size() / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                    g2.setColor(Color.BLACK);
                    String xLabel = i + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }

        // create x and y axes
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor1);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints1.size() - 1; i++) {
            int x1 = graphPoints1.get(i).x;
            int y1 = graphPoints1.get(i).y;
            int x2 = graphPoints1.get(i + 1).x;
            int y2 = graphPoints1.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(lineColor2);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints2.size() - 1; i++) {
            int x1 = graphPoints2.get(i).x;
            int y1 = graphPoints2.get(i).y;
            int x2 = graphPoints2.get(i + 1).x;
            int y2 = graphPoints2.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(lineColor3);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints3.size() - 1; i++) {
            int x1 = graphPoints3.get(i).x;
            int y1 = graphPoints3.get(i).y;
            int x2 = graphPoints3.get(i + 1).x;
            int y2 = graphPoints3.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (int i = 0; i < graphPoints1.size(); i++) {
            int x = graphPoints1.get(i).x - pointWidth / 2;
            int y = graphPoints1.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 100);
    }

    private double getMinValue() {
        double minValue = Double.MAX_VALUE;
        for (Double value : ogmValues) {
            minValue = Math.min(minValue, value);
        }
        return minValue;
    }

    private double getMaxValue() {
        double maxScore = Double.MIN_VALUE;
        for (Double value : ogmValues) {
            maxScore = Math.max(maxScore, value);
        }
        return maxScore;
    }

    void setValues(List<Double> val1, List<Double> val2) {
        this.ogmValues = val1;
        this.ucmValues = val2;
        this.totalValues.clear();
        invalidate();
        System.out.println("PRINT STATS!");
        this.repaint();
    }

}