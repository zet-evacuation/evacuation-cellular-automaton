package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.potential.DynamicPotential;

/**
 * Provides rules with computed values that may be necessary. Computations take into account the current state.
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Computation {

    double effectivePotential(EvacCell referenceCell, EvacCell targetCell, DynamicPotential dynamicPotential);

    double updateExhaustion(Individual individual, EvacCell targetCell);

    double updatePreferredSpeed(Individual individual);

    double updatePanic(Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells);

    /* Threshold values for various decisions */
    public double changePotentialThreshold(Individual individual);

    public double movementThreshold(Individual individual);
    public double idleThreshold(Individual i);


}
