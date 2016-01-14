package org.zet.cellularautomaton.algorithm;

import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.Individual;

/**
 * Provides actions to change the evacuation state. Alters the state of the simulation and of individuals and ensures
 * that after each action the simulation remains in a well-defined state.
 * 
 * @author Jan-Philipp Kappmeier
 */

public class EvacuationStateController implements EvacuationStateControllerInterface {
    private final MutableEvacuationState evacuationState;

    public EvacuationStateController(MutableEvacuationState evacuationState) {
        this.evacuationState = evacuationState;
    }

    /**
     * Sets an individual dead. The individual is taken out of the simulation.
     * 
     * @param i an individual
     * @param cause the reason why the individual dies.
     */
    @Override
    public void die(Individual i, DeathCause cause) {
        evacuationState.getIndividualState().die(i);
        evacuationState.propertyFor(i).setDeathCause(cause);
    }
    
}
