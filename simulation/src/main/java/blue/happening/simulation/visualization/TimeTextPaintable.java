/*
 * ManetSim - http://www.pages.drexel.edu/~sf69/sim.html
 * 
 * Copyright (C) 2010  Semyon Fishman
 * 
 * This file is part of ManetSim.
 * 
 * ManetSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ManetSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ManetSim.  If not, see <http://www.gnu.org/licenses/>.
 */

package blue.happening.simulation.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Locale;

import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;


// TODO: this file need some serious clean up.
public class TimeTextPaintable<V, E> implements Paintable {

    private final BasicVisualizationServer<V, E> visualizationServer;
    private Font font;
    private FontMetrics metrics;

    private long startTime = 0;

    TimeTextPaintable(BasicVisualizationServer<V, E> visualizationServer) {
        this.visualizationServer = visualizationServer;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void paint(Graphics g) {
        Dimension d = visualizationServer.getSize();
        if (font == null) {
            font = new Font(g.getFont().getName(), Font.BOLD, 30);
            metrics = g.getFontMetrics(font);
        }

        int time = (int) (System.currentTimeMillis() - startTime);
        int minutes = time / (60 * 1000);
        int seconds = (time / 1000) % 60;
        String str = String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds);

        int swidth = metrics.stringWidth(str);
        int sheight = metrics.getMaxAscent() + metrics.getMaxDescent();
        int x = (d.width - swidth) / 2;
        int y = (int) (d.height - sheight * 1.5);
        g.setFont(font);
        Color oldColor = g.getColor();
        g.setColor(Color.BLACK);
        g.drawString(str, x, y);
        g.setColor(oldColor);
    }

    @Override
    public boolean useTransform() {
        return false;
    }

}
