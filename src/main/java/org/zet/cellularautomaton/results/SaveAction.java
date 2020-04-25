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
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.results.Action.CADoesNotMatchException;

/**
 * Represents the fact that an individual leaves the simulation. Note that this action starts and ends on the same cell.
 * The performing individual is the individual that occupies the exit cell.
 *
 * @author Daniel R. Schmidt
 */
public class SaveAction extends Action {

    /** The cell where an individual leaves the simulation. */
    protected Individual savedIndividual;
    private final int timeStep;

    /**
     * Creates a new Exit action.
     *
     * @param savedIndividual the cell from where the individual leaves the system.
     * @param timeStep
     */
    public SaveAction(Individual savedIndividual, int timeStep) {
        this.savedIndividual = savedIndividual;
        this.timeStep = timeStep;
    }

    @Override
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        ec.setSafe(savedIndividual);
        es.propertyFor(savedIndividual).setPanic(0);
    }

    @Override
    public void executeDelayed(EvacuationState es, EvacuationStateControllerInterface ec) {
    }

    @Override
    public String toString() {
        return "Individual " + savedIndividual + " is saved.";
    }

    public Individual getSavedIndividual() {
        return savedIndividual;
    }

    public int getTimeStep() {
        return timeStep;
    }

}
