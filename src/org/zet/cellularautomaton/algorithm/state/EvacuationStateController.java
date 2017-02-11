package org.zet.cellularautomaton.algorithm.state;

import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCellInterface;
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

    @Override
    public void move(EvacCellInterface from, EvacCellInterface to) {
        Individual i = getAndCheck(from);
        if (from.equals(to)) {
            return;
        }
        remove(i);
        add(i, to);
    }

    @Override
    public void swap(EvacCellInterface from, EvacCellInterface to) {
        Individual i1 = getAndCheck(from);
        Individual i2 = getAndCheck(to);
        if (from.equals(to)) {
            return;
        }
        remove(i1);
        remove(i2);
        add(i2, from);
        add(i1, to);
    }
    
    private Individual getAndCheck(EvacCellInterface cell) {
        if (cell.getState().isEmpty()) {
            throw new IllegalArgumentException("No Individual standing on cell " + cell);
        }
        return cell.getState().getIndividual();
    }

    void add(Individual i, EvacCellInterface cell) {
        evacuationState.propertyFor(i).setCell(cell);
        cell.getState().setIndividual(i);
        cell.getRoom().addIndividual(cell, i);
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
        evacuationState.addToEvacuated(i);
        remove(i);
    }

    /**
     * Removes an individual. This has to be called when an individual dies, is evacuated, moved or swapped.
     * @param i an individual
     */
    void remove(Individual i) {
        EvacCellInterface from = evacuationState.propertyFor(i).getCell();
        from.getRoom().removeIndividual(i);
        evacuationState.propertyFor(i).setCell(null);
        from.getState().removeIndividual();
    }

    @Override
    public void increaseDynamicPotential(EvacCellInterface c) {
        evacuationState.increaseDynamicPotential(c);
    }

    @Override
    public void updateDynamicPotential(double probabilityDynamicIncrease, double probabilityDynamicDecrease) {
        evacuationState.updateDynamicPotential(probabilityDynamicIncrease, probabilityDynamicDecrease);
    }
    
    
    
}