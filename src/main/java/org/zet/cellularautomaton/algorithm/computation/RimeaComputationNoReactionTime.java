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
package org.zet.cellularautomaton.algorithm.computation;

import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RimeaComputationNoReactionTime extends RimeaComputation {

    public RimeaComputationNoReactionTime(PropertyAccess es, ParameterSet parameterSet) {
        super(es, parameterSet);
    }

    /**
     *
     * @param individual
     * @return 0.0
     */
    @Override
    public double idleThreshold(Individual individual) {
        return 0.0;
    }

}
