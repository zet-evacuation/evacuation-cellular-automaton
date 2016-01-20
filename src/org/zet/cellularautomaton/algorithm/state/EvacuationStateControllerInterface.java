package org.zet.cellularautomaton.algorithm.state;

import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;

/**
 * Performs actions in an evacuation run on a evacuation simulation. Each action that can be
 * executed ensures that the evacuation state remains in a well-defined state.
 * 
 * @author Jan-Philipp Kappmeier
 */
public interface EvacuationStateControllerInterface {

    /**
     * Moves an individual from the origin cell to the target cell. When the method is called,
     * there must be an individual standing on {@code from} and no individual must be standing on
     * {@code to}.
     * 
     * @param from the origin cell
     * @param to the target cell
     */
    public void move(EvacCell from, EvacCell to);

    /**
     * Swaps the positions of two individuals between two cells. Both cells, {@code cell1} and
     * {@code cell2} must be occupied with an individual when the method is called.
     * 
     * @param cell1 the first cell with an individual
     * @param cell2 the second cell with an individual
     */
    public void swap(EvacCell cell1, EvacCell cell2);

    /**
     * Sets an individual to be dead. The reason is stored and the individual is taken out of the
     * simulation. A dead individual can never be saved or evacuated.
     * 
     * @param individual the individual that dies
     * @param cause the reason
     */
    public void die(Individual individual, DeathCause cause);

    /**
     * Sets an individual to be safe. An individual can be safe but not yet evacuated. It can not
     * die any more.
     * 
     * @param individual the saved individual
     */
    public void setSafe(Individual individual);

    /**
     * Sets an individual evacuated. This automatically also sets the individual safe. When an
     * individual is evacuated it can not die any more.
     * 
     * @param individual the evacuated individual
     */
    public void evacuate(Individual individual);
}
