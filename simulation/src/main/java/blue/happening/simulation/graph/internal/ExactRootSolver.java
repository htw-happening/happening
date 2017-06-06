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


/**
 * Static class, provides functions for solving for the roots of polynomials.
 * This class provides functions for solving up to second-order polynomials. You
 * may call the functions for the specific polynomial you have, or the generic
 * {@code roots} function which will than delegate to the appropriate function.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class ExactRootSolver {

    private ExactRootSolver() {
        // prevent instantiation, this is a static class
    }

    public static double[] roots(PolynomialFunction polynomial) {

        switch (polynomial.getCoefficients().length) {
            case 1:
                return new double[]{};
            case 2:
                return new double[]{linearRoot(polynomial)};
            case 3:
                return quadraticRoots(polynomial);
            default:
                throw new IllegalArgumentException(
                        "currently only polynomials of 3 of less coefficents are suppoerted but polynomial has "
                                + polynomial.getCoefficients().length + " coefficeints");
        }
    }

    public static RootSolution[] rootSolutions(PolynomialFunction polynomial) {

        switch (polynomial.getCoefficients().length) {
            case 1:
                return new RootSolution[]{};
            case 2:
                return new RootSolution[]{linearRootSolutions(polynomial)};
            case 3:
                return quadraticRootSolutions(polynomial);
            default:
                throw new IllegalArgumentException(
                        "currently only polynomials of 3 of less coefficents are suppoerted but polynomial has "
                                + polynomial.getCoefficients().length + " coefficeints");
        }
    }

    public static double linearRoot(PolynomialFunction linear) {
        double c[] = linear.getCoefficients();
        if (c.length != 2)
            throw new IllegalArgumentException(
                    "linear must be a polynomial of exactly 2 coefficeints but has "
                            + c.length);
        return linearRoot(c[0], c[1]);
    }

    public static double linearRoot(double b, double m) {
        return -b / m;
    }

    public static RootSolution linearRootSolutions(PolynomialFunction linear) {
        final double root = linearRoot(linear);
        final PolynomialFunction derivative = linear.polynomialDerivative();
        final double deriv = derivative.value(root);
        return new RootSolution(root, deriv);
    }

    public static double[] quadraticRoots(PolynomialFunction quadratic) {
        double c[] = quadratic.getCoefficients();
        if (c.length != 3)
            throw new IllegalArgumentException(
                    "quadratic must be a polynomial of exactly 3 coefficeints but has "
                            + c.length);
        return quadraticRoots(c[0], c[1], c[2]);
    }

    public static double[] quadraticRoots(double c, double b, double a) {
        final double delta = (b * b) - (4 * a * c); // the discriminant
        final double sqrtDelta = Math.sqrt(delta);
        final double[] roots;

        if (delta > 0) {
            // If the discriminant is positive,
            // there are two distinct roots,
            // both of which are real numbers.
            roots = new double[2];
            roots[0] = (-b + sqrtDelta) / (2.0 * a);
            roots[1] = (-b - sqrtDelta) / (2.0 * a);

        } else if (delta == 0) {
            // If the discriminant is zero,
            // there is exactly one distinct
            // real root
            roots = new double[1];
            roots[0] = -b / (2 * a);

        } else {
            // If the discriminant is negative,
            // there are no real roots.
            roots = new double[0];
        }

        return roots;
    }

    public static RootSolution[] quadraticRootSolutions(
            PolynomialFunction quadratic) {

        final double[] roots = quadraticRoots(quadratic);
        final RootSolution[] rootSolutions = new RootSolution[roots.length];

        if (roots.length == 1) {
            rootSolutions[0] = new RootSolution(roots[0], 0);
        } else {
            PolynomialFunction derivative = quadratic.polynomialDerivative();
            for (int i = 0; i < roots.length; i++) {
                final double root = roots[i];
                final double deriv = derivative.value(root);
                rootSolutions[i] = new RootSolution(root, deriv);
            }
        }

        return rootSolutions;
    }

    /**
     * A container class for transferring root solutions along with their
     * derivatives.
     *
     * @author Semyon Fishman (sf69@drexel.edu)
     */
    public static class RootSolution {

        private final double value;
        private final double derivative;

        public RootSolution(final double value, final double derivative) {
            this.value = value;
            this.derivative = derivative;
        }

        public double getValue() {
            return value;
        }

        public double getDerivative() {
            return derivative;
        }

    }
}
