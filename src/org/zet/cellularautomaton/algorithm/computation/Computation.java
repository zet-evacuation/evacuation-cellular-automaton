package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
import java.util.function.Function;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;

/**
 * Provides rules with computed values that may be necessary. Computations take into account the current state.
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Computation {

    double effectivePotential(Individual individual, EvacCell targetCell, Function<EvacCell,Double> dynamicPotential);

    double updatePreferredSpeed(Individual individual);

    double updateExhaustion(Individual individual, EvacCell targetCell);

    double updatePanic(Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells);

    /* Threshold values for various decisions */
    public double changePotentialThreshold(Individual individual);

    public double idleThreshold(Individual i);

}
