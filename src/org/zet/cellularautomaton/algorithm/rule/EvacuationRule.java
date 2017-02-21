/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
package org.zet.cellularautomaton.algorithm.rule;

import java.util.Optional;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.results.Action;
import org.zetool.algorithm.simulation.cellularautomaton.Rule;

/**
 * @param <R> the result action
 * @author Daniel R. Schmidt
 */
public interface EvacuationRule<R extends Action> extends Rule<R, EvacCellInterface> {

    @Override
    Optional<R> execute(EvacCellInterface cell);

    @Override
    boolean executableOn(EvacCellInterface cell);

    void setEvacuationState(EvacuationState es);
    void setComputation(Computation c);
    void setEvacuationSimulationSpeed(EvacuationSimulationSpeed sp);
}
