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

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

import java.util.ArrayList;
import java.util.List;

import blue.happening.simulation.graph.NetworkGraph;

import static java.lang.Math.pow;


/**
 * This class represents physical distance between two vertices.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class VerticesDistance<V, E> {

    private final NetworkGraph<V, E> graph;
    private final V v1;
    private final V v2;

    public VerticesDistance(final NetworkGraph<V, E> graph, final V v1,
                            final V v2) {
        this.graph = graph;
        this.v1 = v1;
        this.v2 = v2;
    }

    public static <V, E> PolynomialFunction polynomial(
            final NetworkGraph<V, E> graph, final V v1, final V v2) {
        return polynomial(graph, v1, v2, 0);
    }

    public static <V, E> PolynomialFunction polynomial(
            final NetworkGraph<V, E> graph, final V v1, final V v2,
            final double radius) {

        final double Sx1 = graph.getStartDisplacementX(v1);
        final double Vx1 = graph.getVelocityX(v1);
        final double Sy1 = graph.getStartDisplacementY(v1);
        final double Vy1 = graph.getVelocityY(v1);
        final double T1 = graph.getTimeStart(v1);

        final double Sx2 = graph.getStartDisplacementX(v2);
        final double Vx2 = graph.getVelocityX(v2);
        final double Sy2 = graph.getStartDisplacementY(v2);
        final double Vy2 = graph.getVelocityY(v2);
        final double T2 = graph.getTimeStart(v2);

        final double c0 = pow(Sx1 - Vx1 * T1 - Sx2 + Vx2 * T2, 0.2e1) + pow(
                Sy1 - Vy1 * T1 - Sy2 + Vy2 * T2, 0.2e1) - (radius * radius);
        final double c1 =
                2 * (Sx1 - Vx1 * T1 - Sx2 + Vx2 * T2) * (Vx1 - Vx2) + 2 * (
                        Sy1 - Vy1 * T1 - Sy2 + Vy2 * T2) * (Vy1 - Vy2);
        final double c2 = pow(Vx1 - Vx2, 0.2e1) + Math.pow(Vy1 - Vy2, 0.2e1);

        return new PolynomialFunction(new double[]{c0, c1, c2});
    }

    private static double calculateDistance(double x1, double y1, double x2,
                                            double y2) {

        final double x = x2 - x1;
        final double y = y2 - y1;
        return Math.hypot(x, y);
    }

    public boolean isFixed() {
        double vx1 = graph.getDisplacementX(v1);
        double vy1 = graph.getDisplacementY(v1);
        double vx2 = graph.getDisplacementX(v2);
        double vy2 = graph.getDisplacementY(v2);

        return (vx1 == vx2 && vy1 == vy2);
    }

    public double getTStart() {
        final double t1 = graph.getTimeStart(v1);
        final double t2 = graph.getTimeStart(v2);
        return Math.max(t1, t2);
    }

    public double getTEnd() {
        final double t1 = graph.getTimeEnd(v1);
        final double t2 = graph.getTimeEnd(v2);
        return Math.min(t1, t2);
    }

    public double getDistance() {

        double x1 = graph.getDisplacementX(v1);
        double y1 = graph.getDisplacementY(v1);
        double x2 = graph.getDisplacementX(v2);
        double y2 = graph.getDisplacementY(v2);

        return calculateDistance(x1, y1, x2, y2);
    }

    public double getInitialDistance() {

        double x1 = graph.getInitialDisplacementX(v1);
        double y1 = graph.getInitialDisplacementY(v1);
        double x2 = graph.getInitialDisplacementX(v2);
        double y2 = graph.getInitialDisplacementY(v2);

        return calculateDistance(x1, y1, x2, y2);
    }

    public double getStartDistance() {

        double x1 = graph.getStartDisplacementX(v1);
        double y1 = graph.getStartDisplacementY(v1);
        double x2 = graph.getStartDisplacementX(v2);
        double y2 = graph.getStartDisplacementY(v2);
        return calculateDistance(x1, y1, x2, y2);
    }

    public List<ExactRootSolver.RootSolution> calcTimesAt(double d) {
        final PolynomialFunction polynomial = polynomial(graph, v1, v2, d);
        final ExactRootSolver.RootSolution[] rootSolutions = ExactRootSolver
                .rootSolutions(polynomial);
        final List<ExactRootSolver.RootSolution> list = new ArrayList<ExactRootSolver.RootSolution>(
                rootSolutions.length);
        final double tStart = getTStart();
        final double tEnd = getTEnd();

        // keep only valid times
        for (ExactRootSolver.RootSolution rootSolution : rootSolutions) {
            final double value = rootSolution.getValue();
            if ((tStart <= value) && (value <= tEnd))
                list.add(rootSolution);
        }

        return list;
    }
}
