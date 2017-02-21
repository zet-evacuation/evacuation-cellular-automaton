/* zet evacuation tool copyright (c) 2007-15 zet evacuation team
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
package org.zet.cellularautomaton.results;

import java.util.Map;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 * Represents the fact that an individual leaves the simulation. Note that this action starts and ends on the same cell.
 * The performing individual is the individual that occupies the exit cell.
 *
 * @author Daniel R. Schmidt
 */
public class ExitAction extends Action {

    /** The cell where an individual leaves the simulation. */
    protected ExitCell exit;
    private final int timeStep;
    private final Individual individual;

    /**
     * Creates a new Exit action.
     *
     * @param exit The cell from where the individual leaves the system.
     */
    public ExitAction(ExitCell exit, int timeStep) {
        this.exit = exit;
        this.timeStep = timeStep;
        this.individual = exit.getState().getIndividual();
    }

    @Override
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        if (adoptCell(exit).getState().isEmpty()) {
            throw new InconsistentPlaybackStateException("Could not evacuate an individual from cell " + adoptCell(exit) + ") because there was none.");
        }
    }

    @Override
    public void executeDelayed(EvacuationState es, EvacuationStateControllerInterface ec) {
        ec.evacuate(individual);
    }

    @Override
    public String toString() {
        return "An individual leaves the simulation from cell " + adoptCell(exit);
    }

    public Individual getIndividual() {
        return individual;
    }

}
