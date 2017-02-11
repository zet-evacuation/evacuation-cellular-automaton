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

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.results.Action.CADoesNotMatchException;

/**
 * Represents the fact that an individual leaves the simulation. Note that this
 * action starts and ends on the same cell. The performing individual is the
 * individual that occupies the exit cell.
 *
 * @author Daniel R. Schmidt
 */
public class SaveAction extends Action {

    /** The cell where an individual leaves the simulation. */
    protected EvacCellInterface cell;
    private final int timeStep;

    /**
     * Creates a new Exit action.
     *
     * @param exit The cell from where the individual leaves the system.
     */
    public SaveAction(EvacCellInterface exit, int timeStep) {
        this.cell = exit;
        this.timeStep = timeStep;
    }

    @Override
    public void execute(EvacuationCellularAutomaton onCA, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        if (cell.getState().isEmpty()) {
            throw new InconsistentPlaybackStateException("Could not evacuate an individual from cell " + cell + "(" + cell.hashCode() + ") because there was none.");
        }
        Individual savedIndividual = cell.getState().getIndividual();
        ec.setSafe(savedIndividual);
        es.propertyFor(savedIndividual).setPanic(0);
        
        //onCA.setIndividualEvacuated(exit.getState().getIndividual());
    }

    @Override
    public void executeDelayed(EvacuationState es) {
    }

    @Override
    public String toString() {
        return "An individual becomes save on cell " + cell;
    }

    @Override
    Action adoptToCA(EvacuationCellularAutomaton targetCA) throws CADoesNotMatchException {
        return VoidAction.VOID_ACTION;
    }
}
