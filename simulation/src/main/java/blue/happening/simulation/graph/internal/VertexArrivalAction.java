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

package blue.happening.simulation.graph.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.internal.ExactRootSolver.RootSolution;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.Waypoint;
import jsl.modeling.ActionListenerIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.Scheduler;


/**
 * This class implements the vertex arrival event. For the interested developer,
 * you will find here much of what is explained in my whitepaper. You can read
 * it <a href="http://www.pages.drexel.edu/~sf69/sim_whitepaper.pdf">here</a>.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class VertexArrivalAction<V, E> implements ActionListenerIfc {

    private static Logger logger = LogManager.getLogger(VertexArrivalAction.class);

    private final NetworkGraph<V, E> graph;
    private final VertexProperties<V, E> vertexProperties;
    private final Scheduler scheduler;

    public VertexArrivalAction(final VertexProperties<V, E> vertexProperties) {
        this.vertexProperties = vertexProperties;
        this.graph = vertexProperties.getNetworkGraph();
        this.scheduler = vertexProperties.getCurrentReplication().getScheduler();
    }

    @Override
    public void action(final JSLEvent event) {
        setNewDirection();
        scheduleEdgeEvents();
        rescheduleThisEvent(event);
    }

    private void setNewDirection() {
        final double tEnd = vertexProperties.getTEnd().getValue();
        final double tStart = vertexProperties.getTStart().getValue();
        double travelTime = tEnd - tStart;

        // update displacement variables
        final double sx = vertexProperties.getSx().getValue();
        final double vx = vertexProperties.getVx().getValue();
        final double newSx = Motion.solveForFinalDisplacement(sx, vx, travelTime);
        vertexProperties.getSx().setValue(newSx);

        final double sy = vertexProperties.getSy().getValue();
        final double vy = vertexProperties.getVy().getValue();
        final double newSy = Motion.solveForFinalDisplacement(sy, vy, travelTime);
        vertexProperties.getSy().setValue(newSy);

        // update start time stamp
        final double newTStart = vertexProperties.getTEnd().getValue();
        vertexProperties.getTStart().setValue(newTStart);

        // the rest of the variables are updated
        // according to the mobilityPattern
        final MobilityPattern<V, E> mobilityPattern = vertexProperties
                .getMobilityPattern();
        final NetworkGraph<V, E> graph = vertexProperties.getNetworkGraph();
        final V vertex = vertexProperties.getVertex();
        final Waypoint<V, E> nextWaypoint = mobilityPattern
                .nextWaypoint(graph, vertex);

        // set new velocities
        final double newVx = nextWaypoint.getVelocityX(graph, vertex);
        vertexProperties.getVx().setValue(newVx);
        final double newVy = nextWaypoint.getVelocityY(graph, vertex);
        vertexProperties.getVy().setValue(newVy);

        // set new travel time
        final double newTravelTime = nextWaypoint.getTravelTime(graph, vertex);
        final double newTEnd = tEnd + newTravelTime;
        vertexProperties.getTEnd().setValue(newTEnd);
    }

    private void scheduleEdgeEvents() {
        final Collection<V> vertices = graph.getVertices();

        for (V other : vertices) {
            if (vertexProperties.getVertex() == other)
                continue;

            scheduleEdgeEvents(vertexProperties.getVertex(), other);
            scheduleEdgeEvents(other, vertexProperties.getVertex());
        }
    }

    private void scheduleEdgeEvents(V from, V to) {
        final VerticesDistance<V, E> distance = new VerticesDistance<V, E>(graph,
                from, to);
        final double tStart = distance.getTStart();
        final double tEnd = distance.getTEnd();

        if (tStart == tEnd)
            return;

        final double txRadius = graph.getRadiusTx(from, to);
        final List<RootSolution> roots = distance.calcTimesAt(txRadius);

        for (RootSolution root : roots) {

            // cases 1, 2, and 3
            if (root.getDerivative() > 0) {
                // case 1
                if (tStart == root.getValue())
                    scheduleRemoveEdgeEvent(from, to, root.getValue());
                    // case 2
                else if (tStart < root.getValue() && root.getValue() < tEnd)
                    scheduleRemoveEdgeEvent(from, to, root.getValue());
                    // case 3
                else if (root.getValue() == tEnd)
                    continue; // ignore
                else
                    throw new AssertionError();
            }
            // cases 4, 5, and 6
            else if (root.getDerivative() == 0) {
                // case 4
                if (tStart == root.getValue())
                    scheduleRemoveEdgeEvent(from, to, root.getValue());
                    // case 5
                else if (tStart < root.getValue() && root.getValue() < tEnd)
                    continue; // ignore
                    // case 6
                else if (root.getValue() == tEnd)
                    scheduleAddEdgeEvent(from, to, root.getValue());
                else
                    throw new AssertionError();
            }
            // cases 7, 8, and 9
            else if (root.getDerivative() < 0) {
                // case 7
                if (tStart == root.getValue())
                    continue; // ignore
                    // case 8
                else if (tStart < root.getValue() && root.getValue() < tEnd)
                    scheduleAddEdgeEvent(from, to, root.getValue());
                    // case 9
                else if (root.getValue() == tEnd)
                    scheduleAddEdgeEvent(from, to, root.getValue());
                else
                    throw new AssertionError();
            } else
                throw new AssertionError();

        }
    }

    private void scheduleAddEdgeEvent(final V from, final V to, double time) {

        ActionListenerIfc addEdgeAction = new ActionListenerIfc() {
            @Override
            public void action(JSLEvent evt) {
                graph.addEdge(from, to);
            }
        };

        final double timeDelta = time - ModelElement.getTime();
        scheduler.scheduleEvent(vertexProperties, addEdgeAction, timeDelta, "",
                JSLEvent.DEFAULT_PRIORITY, null);

        logger.debug(
                "Scheduled edge addition event at time " + time + " from vertex '"
                        + from + "' to vertex '" + to + "'");
    }

    private void scheduleRemoveEdgeEvent(final V from, final V to, double time) {

        ActionListenerIfc removeEdgeAction = new ActionListenerIfc() {
            @Override
            public void action(JSLEvent evt) {
                graph.removeEdge(from, to);
            }
        };

        final double timeDelta = time - ModelElement.getTime();
        scheduler.scheduleEvent(vertexProperties, removeEdgeAction, timeDelta, "",
                JSLEvent.DEFAULT_PRIORITY, null);

        logger.debug(
                "Scheduled edge removal event at time " + time + " from vertex '" + from
                        + "' to vertex '" + to + "'");
    }

    private void rescheduleThisEvent(final JSLEvent event) {
        final Scheduler scheduler = vertexProperties.getCurrentReplication()
                .getScheduler();
        final double tEnd = vertexProperties.getTEnd().getValue();
        final double tNow = ModelElement.getTime();
        final double tDelta = tEnd - tNow;
        scheduler.reschedule(event, tDelta);
    }
}
