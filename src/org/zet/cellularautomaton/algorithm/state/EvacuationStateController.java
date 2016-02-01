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
    
    void add(Individual i, EvacCell cell) {
        evacuationState.propertyFor(i).setCell(cell);
        cell.getState().setIndividual(i);
    } 

    @Override
    public void move(EvacCell from, EvacCell to) {
        Individual i = from.getState().getIndividual();
        remove(i);
        add(i, to);
    }

    @Override
    public void swap(EvacCell from, EvacCell to) {
        Individual i1 = from.getState().getIndividual();
        Individual i2 = from.getState().getIndividual();
        remove(i1);
        remove(i2);
        add(i2, from);
        add(i1, to);
    }

    @Override
    public void die(Individual i, DeathCause cause) {
        evacuationState.propertyFor(i).setDeathCause(cause);
        evacuationState.addToDead(i);
        remove(i);
    }
    
    @Override
    public void setSafe(Individual i) {
        evacuationState.propertyFor(i).setSafetyTime(evacuationState.getStep());
        evacuationState.addToSafe(i);
    }
    
    @Override
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

    @Override
    public void increaseDynamicPotential(EvacCell c) {
        evacuationState.increaseDynamicPotential(c);
    }

    @Override
    public void updateDynamicPotential(double probabilityDynamicIncrease, double probabilityDynamicDecrease) {
        evacuationState.updateDynamicPotential(probabilityDynamicIncrease, probabilityDynamicDecrease);
    }
    
    
    
}