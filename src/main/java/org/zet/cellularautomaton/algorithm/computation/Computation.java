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

import java.util.Collection;
import java.util.function.Function;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;

/**
 * Provides rules with computed values that may be necessary. Computations take into account the current state.
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Computation {

    double effectivePotential(Individual individual, EvacCellInterface targetCell, Function<EvacCellInterface,Double> dynamicPotential);

    double updatePreferredSpeed(Individual individual);

    double updateExhaustion(Individual individual, EvacCellInterface targetCell);

    double updatePanic(Individual individual, EvacCellInterface targetCell, Collection<EvacCellInterface> preferedCells);

    /* Threshold values for various decisions */
    public double changePotentialThreshold(Individual individual);

    public double idleThreshold(Individual i);

}
