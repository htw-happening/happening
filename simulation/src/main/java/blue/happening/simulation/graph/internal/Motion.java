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

/**
 * This class represents linear motion. It is used to solve for various values
 * such as the final displacement, velocity, et cetra.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class Motion {

    public Motion(final double initialDisplacement, final double velocity,
                  final double initialTime) {

    }

    public static double solveForInitialDisplacement(final double sf,
                                                     final double v, final double t) {

        return sf - (v * t);
    }

    public static double solveForFinalDisplacement(final double si,
                                                   final double v, final double t) {

        return si + (v * t);
    }

    public static double solveForVelocity(final double si, final double sf,
                                          final double t) {

        return (sf - si) / t;
    }

    public static double solveForTime(final double si, final double sf,
                                      final double v) {

        return (sf - si) / v;
    }
}
