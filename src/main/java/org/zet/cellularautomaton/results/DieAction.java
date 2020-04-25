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

import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 * Represents the fact that an individual died.
 * @author Daniel R. Schmidt
 */
public class DieAction extends Action {

    /** The cell on which the individual stood when it died. */
    private final EvacCellInterface placeOfDeath;
    /** The cause which caused the individuals dead. */
    private final DeathCause cause;
    /** The number of the individual. Is needed for visualization. */
    private final Individual individual;

    /**
     * Creates a new instance of the DyingAction which represents the dead of an individual during the evacuation.
     *
     * @param placeOfDeath the cell on which the individual stands
     * @param cause the cause of the death
     * @param individual the individual
     */
    public DieAction(EvacCellInterface placeOfDeath, DeathCause cause, Individual individual) {
        this.placeOfDeath = placeOfDeath;
        this.cause = cause;
        this.individual = individual;
    }

    /**
     * Returns the cell on which the individual stood
     *
     * @return the cell on which the individual stood
     */
    public EvacCellInterface placeOfDeath() {
        return placeOfDeath;
    }

    /**
     * Returns the number of the individual, can be used to access the individuals as the number is unique.
     *
     * @return the number of the individual
     */
    public int getIndividualNumber() {
        return individual.id();
    }

    /**
     * {@inheritDoc}
     *
     * @param es the evacuation state
     * @throws InconsistentPlaybackStateException if the individual that is to die is not on the cell
     * @see ds.ca.results.Action#execute(ds.ca.EvacuationCellularAutomaton)
     */
    @Override
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        if (placeOfDeath.getState().isEmpty()) {
            throw new InconsistentPlaybackStateException(
                    "I could not mark the individual on cell "
                    + "as dead because it was not there (someone was lucky there, hu?)");
        }

        ec.die(individual, cause);
    }

    @Override
    public void executeDelayed(EvacuationState es, EvacuationStateControllerInterface ec) {
    }

    /**
     * {@inheritDoc}
     *
     * @return text description of the action
     * @see ds.ca.results.Action#toString()
     */
    @Override
    public String toString() {
        String representation = "";

        representation += "An individual dies on cell ";
        representation += placeOfDeath;
        representation += " because of ";
        representation += cause;

        return representation;
    }

    public DeathCause getDeathCause() {
        return cause;
    }

    public Individual getIndividual() {
        return individual;
    }

    public EvacCellInterface getPlaceOfDeath() {
        return placeOfDeath;
    }
    
    
}
