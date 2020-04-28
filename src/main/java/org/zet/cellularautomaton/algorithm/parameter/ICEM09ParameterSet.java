/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.algorithm.parameter;

/**
 * @author Sylvie Temme
 */
public class ICEM09ParameterSet extends ParameterSetAdapter {

    /**
     * Initializes the default parameter set and loads some constants from the property container.
     */
    public ICEM09ParameterSet() {
        super(getSafe("algo.ca.PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO", 0), getSafe("algo.ca.SLACKNESS_TO_IDLE_RATIO", 0),
        getSafe("algo.ca.PANIC_DECREASE", 0.0), getSafe("algo.ca.PANIC_INCREASE", 0.0), getSafe("algo.ca.PANIC_WEIGHT_ON_SPEED", 0),
        getSafe("algo.ca.PANIC_WEIGHT_ON_POTENTIALS", 0), getSafe("algo.ca.EXHAUSTION_WEIGHT_ON_SPEED", 0),
        getSafe("algo.ca.PANIC_THRESHOLD", 3));
    }

    ////* Conversion parameters *////
    @Override
    public double getSpeedFromAge(double pAge) {
        return 0.595;
    }

    @Override
    public double getSlacknessFromDecisiveness(double pDecisiveness) {
        return 0;
    }

    @Override
    public double getExhaustionFromAge(double pAge) {
        return 0;
    }

    @Override
    public double getReactionTimeFromAge(double pAge) {
        return 0;
    }
}
