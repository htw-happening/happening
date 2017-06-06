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

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;


public class TimedJComponenetRepainter {

    public TimedJComponenetRepainter(final JComponent component, long period) {

        Timer timer = new Timer();
        TimerTask repaintTask = new TimerTask() {

            @Override
            public void run() {
                component.repaint();
            }
        };

        timer.schedule(repaintTask, 0, period);

    }

}
