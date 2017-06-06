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

package blue.happening.simulation.logging;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;


/**
 * This class extends log4j's {@link PatternLayout} by adding support for
 * outputting <em>simulated time</em> in log4j.
 * <p>
 * It adds one new conversion character, <b>T</b>, used to output the the
 * simulated time.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class JSLPatternLayout extends PatternLayout {

    /**
     * Same as {@link PatternLayout#PatternLayout()}
     */
    public JSLPatternLayout() {
        super();
    }

    /**
     * Same as {@link PatternLayout#PatternLayout(String)}
     */
    public JSLPatternLayout(String pattern) {
        super(pattern);
    }

    @Override
    protected PatternParser createPatternParser(String pattern) {

        PatternParser result;
        if (pattern == null)
            result = new JSLPatternParser(DEFAULT_CONVERSION_PATTERN);
        else
            result = new JSLPatternParser(pattern);

        return result;
    }

}
