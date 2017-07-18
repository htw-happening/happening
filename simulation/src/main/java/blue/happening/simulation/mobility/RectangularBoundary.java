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

package blue.happening.simulation.mobility;

import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.internal.Motion;


/**
 * A boundary in the shape of a rectangle.
 *
 * @param <V>
 * @param <E>
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class RectangularBoundary<V, E> implements Boundary<V, E> {

    double x;
    double y;
    double width;
    double height;

    /**
     * Constructs a new {@code RectangularBoundary} whose upper-left corner is
     * specified by {@code (x,y)} and whose {@code width} and {@code height}.
     *
     * @param x      the x coordinate of the upper-left corner of the rectangle
     * @param y      the y coordinate of the upper-left corner of the rectangle
     * @param width  the width of the Rectangle.
     * @param height the height of the rectangle
     */
    public RectangularBoundary(final double x, final double y, final double width,
                               final double height) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean isInBoundary(NetworkGraph<V, E> graph, V vertex,
                                Waypoint<V, E> waypoint) {
        final boolean xInBound = isXInBound(graph, vertex, waypoint);
        final boolean yInBound = isYInBound(graph, vertex, waypoint);
        return (xInBound && yInBound);
    }

    /**
     * Returns {@code true} if {@code vertex}, after moving to the destination
     * specified by {@code waypoint}, will be within the x-axis boundary of this
     * {@code rectangularBoundary}. Otherwise returns {@code false}.
     *
     * @param graph    the {@code networkgraph} that contains {@code vertex}
     * @param vertex   the vertex to test
     * @param waypoint the waypoint to test
     * @return {@code true} if {@code vertex} will be within the x-axis boundary
     * after traveling to {@code waypoint}, otherwise {@code false}
     */
    public boolean isXInBound(NetworkGraph<V, E> graph, V vertex,
                              Waypoint<V, E> waypoint) {

        final double si = graph.getDisplacementX(vertex);
        final double v = waypoint.getVelocityX(graph, vertex);
        final double t = waypoint.getTravelTime(graph, vertex);
        final double sf = Motion.solveForFinalDisplacement(si, v, t);
        return ((x <= sf) && (sf <= x + width));
    }

    /**
     * Returns {@code true} if {@code vertex}, after moving to the destination
     * specified by {@code waypoint}, will be within the y-axis boundary of this
     * {@code rectangularBoundary}. Otherwise returns {@code false}.
     *
     * @param graph    the {@code networkgraph} that contains {@code vertex}
     * @param vertex   the vertex to test
     * @param waypoint the waypoint to test
     * @return {@code true} if {@code vertex} will be within the y-axis boundary
     * after traveling to {@code waypoint}, otherwise {@code false}
     */
    public boolean isYInBound(NetworkGraph<V, E> graph, V vertex,
                              Waypoint<V, E> waypoint) {
        final double si = graph.getDisplacementY(vertex);
        final double v = waypoint.getVelocityY(graph, vertex);
        final double t = waypoint.getTravelTime(graph, vertex);
        final double sf = Motion.solveForFinalDisplacement(si, v, t);
        return ((y <= sf) && (sf <= y + height));
    }

	/*
     *
	 * @param x the X coordinate of the upper-left corner of the rectangle
	 * 
	 * @param y the Y coordinate of the upper-left corner of the rectangle
	 * 
	 * @param width the width of the Rectangle.
	 * 
	 * @param height the height of the rectangle
	 */

    /**
     * Returns the x-axis upper-left corner of this {@code rectangularBoundary}.
     *
     * @return the x coordinate of the upper-left corner of this
     * {@code rectangularBoundary}
     */
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y-axis upper-left corner of this {@code rectangularBoundary}.
     *
     * @return the y coordinate of the upper-left corner of this
     * {@code rectangularBoundary}
     */
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the width of this {@code rectangularBoundary}.
     *
     * @return the width of this {@code rectangularBoundary}
     */
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Returns the height of this {@code rectangularBoundary}.
     *
     * @return the height of this {@code rectangularBoundary}
     */
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Returns the area of this {@code rectangularBoundary}.
     *
     * @return the area of this {@code rectangularBoundary}
     */
    public double getArea() {
        return (width * height);
    }

}
