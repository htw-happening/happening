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

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

import jsl.modeling.ModelElement;


/**
 * This class extends {@link PatternParser} by adding support for outputting
 * <em>simulated time</em> in log4j.
 * <p>
 * It adds one new conversion character, <b>T</b>, used to output the the
 * simulated time.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class JSLPatternParser extends PatternParser {

    private static final char SIMULATION_TIME = 'T';

    public JSLPatternParser(String pattern) {
        super(pattern);
    }

    /**
     * @see PatternParser#finalizeConverter(char)
     */
    @Override
    protected void finalizeConverter(char formatChar) {

        if (formatChar == SIMULATION_TIME) {
            PatternConverter pc = new SimulationTimePatternConverter(formattingInfo);
            currentLiteral.setLength(0);
            addConverter(pc);
        } else
            super.finalizeConverter(formatChar);
    }

    private static class SimulationTimePatternConverter extends PatternConverter {

        public SimulationTimePatternConverter(FormattingInfo fi) {
            super(fi);
        }

        @Override
        public String convert(LoggingEvent event) {
            return String.valueOf(ModelElement.getTime());
        }
    }
}
