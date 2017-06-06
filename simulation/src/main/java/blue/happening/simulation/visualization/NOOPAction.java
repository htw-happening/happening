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

import jsl.modeling.ActionListenerIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;


public class NOOPAction extends SchedulingElement implements ActionListenerIfc {

    private final double interval;
    private final long sleep;

    public NOOPAction(ModelElement parent, double interval, long sleep) {
        this(parent, null, interval, sleep);
    }

    public NOOPAction(ModelElement parent, String name, double interval,
                      long sleep) {
        super(parent, name);
        this.interval = interval;
        this.sleep = sleep;
    }

    @Override
    protected void initialize() {
        super.initialize();
        scheduleEvent(this, 0, getName() + "-event");
    }

    @Override
    public void action(JSLEvent event) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        rescheduleEvent(event, interval);
    }
}