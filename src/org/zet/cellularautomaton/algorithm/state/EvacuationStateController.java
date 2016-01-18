package org.zet.cellularautomaton.algorithm.state;

import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
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
    
    public void move(EvacCell from, EvacCell to) {
        Individual i = from.getState().getIndividual();
        remove(i);
        add(i, to);
    }
    
    public void swap(EvacCell from, EvacCell to) {
        Individual i1 = from.getState().getIndividual();
        Individual i2 = from.getState().getIndividual();
        remove(i1);
        remove(i2);
        add(i2, from);
        add(i1, to);
    }
    
    void add(Individual i, EvacCell cell) {
        evacuationState.propertyFor(i).setCell(cell);
        cell.getState().setIndividual(i);
    } 

    /**
     * Sets an individual dead. The individual is taken out of the simulation.
     * 
     * @param i an individual
     * @param cause the reason why the individual dies.
     */
    @Override
    public void die(Individual i, DeathCause cause) {
        evacuationState.die(i);
        evacuationState.propertyFor(i).setDeathCause(cause);
        remove(i);
    }
    
    @Override
    public void setSafe(Individual i) {
        evacuationState.propertyFor(i).setSafetyTime(evacuationState.getStep());
        evacuationState.setSafe(i);
    }
    
    public void evacuate(Individual i) {
        evacuationState.propertyFor(i).setEvacuationTime(evacuationState.getStep());
        remove(i);
    }

    /**
     * Removes an individual. This has to be called when an individual dies, is evacuated, moved or swapped.
     * @param i an individual
     */
    void remove(Individual i) {
        
    }
    
}