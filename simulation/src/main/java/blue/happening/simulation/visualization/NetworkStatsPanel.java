package blue.happening.simulation.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
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
    private Color lineColor2 = new Color(204, 0, 9, 219);
    private Color lineColor3 = new Color(38, 204, 0, 219);
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(1f);
    private int pointWidth = 4;
    private int numberYDivisions = 5;
    private List<Double> totalValues;
    private List<Double> inValues;
    private List<Double> outValues;

    NetworkStatsPanel(List<Double> ogm, List<Double> ucm) {
        this.inValues = ogm;
        this.outValues = ucm;
        this.totalValues = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = (double) getWidth() / (inValues.size() - 1);
        double yScale = (double) getHeight() / (getMaxInValue() - getMaxOutValue());

        List<Point> graphPoints1 = new ArrayList<>();
        List<Point> graphPoints2 = new ArrayList<>();
        // List<Point> graphPoints3 = new ArrayList<>();

        for (int i = 0; i < inValues.size(); i++) {
            int x = (int) (i * xScale);

            int y1 = (int) ((getMaxInValue() - inValues.get(i)) * yScale);
            graphPoints1.add(new Point(x, y1));

            int y2 = (int) ((getMaxOutValue() - outValues.get(i)) * yScale);
            graphPoints2.add(new Point(x, y2));

            totalValues.add(i, inValues.get(i) + outValues.get(i));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        //g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = 0;
            int x1 = pointWidth;
            int y0 = getHeight() - ((i * (getHeight() * 2)) / numberYDivisions);
            int y1 = y0;
            if (inValues.size() > 0) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(1 + pointWidth, y0, getWidth(), y1);
                g2.setColor(Color.DARK_GRAY);
                String yLabel = ((int) ((getMaxOutValue() + (getMaxInValue() - getMaxOutValue()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        /*
        // and for x axis
        for (int i = 0; i < inValues.size(); i++) {
            if (inValues.size() > 1) {
                int x0 = i * (getWidth() - padding * 2 - labelPadding) / (inValues.size() - 1) + padding + labelPadding;
                int x1 = x0;
                int y0 = getHeight() - padding - labelPadding;
                int y1 = y0 - pointWidth;
                if ((i % ((int) ((inValues.size() / 20.0)) + 1)) == 0) {
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
        */

        // create x and y axes
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        g2.drawLine(0, 0, 0, getHeight());

        //Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor1);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints1.size() - 1; i++) {
            int x1 = graphPoints1.get(i).x;
            int y1 = graphPoints1.get(i).y;
            int x2 = graphPoints1.get(i + 1).x;
            int y2 = graphPoints1.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
            int[] xpoints = {x1, x1, x2, x2};
            int[] ypoint = {getHeight()/2, y1, y2, getHeight()/2};
            g2.fillPolygon(new Polygon(xpoints, ypoint, 4));
        }

        g2.setColor(lineColor2);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints2.size() - 1; i++) {
            int x1 = graphPoints2.get(i).x;
            int y1 = graphPoints2.get(i).y * -1;
            int x2 = graphPoints2.get(i + 1).x;
            int y2 = graphPoints2.get(i + 1).y * -1;
            g2.drawLine(x1, y1, x2, y2);
            int[] xpoints = {x1, x1, x2, x2};
            int[] ypoint = {getHeight()/2, y1, y2, getHeight()/2};
            g2.fillPolygon(new Polygon(xpoints, ypoint, 4));
        }

        /*
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
        */

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 100);
    }

    private double getMaxInValue() {
        double inValue = Double.MIN_VALUE;
        for (Double value : inValues) {
            inValue = Math.max(inValue, value);
        }
        return inValue;
    }

    private double getMaxOutValue() {
        double outValue = Double.MIN_VALUE;
        for (Double value : outValues) {
            outValue = Math.max(outValue, value);
        }
        return -outValue;
    }

    void setValues(List<Double> val1, List<Double> val2) {
        this.inValues = val1;
        this.outValues = val2;
        invalidate();
        this.repaint();
    }

}