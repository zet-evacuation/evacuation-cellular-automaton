package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
import java.util.Objects;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.potential.DynamicPotential;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimpleComputation implements Computation {

    private final PropertyAccess es;

    public SimpleComputation(PropertyAccess es) {
        this.es = Objects.requireNonNull(es);
    }

    /**
     * Retrieves the static potential for the given individual and computes the difference between the potentials
     * between the two cells. As reference cell the individual's cell is taken.
     * @param individual
     * @param targetCell
     * @param dynamicPotential
     * @return the potential difference between the two cells
     */
    @Override
    public double effectivePotential(Individual individual, EvacCell targetCell, DynamicPotential dynamicPotential) {
        EvacCell referenceCell = es.propertyFor(individual).getCell();
        StaticPotential staticPotential = es.propertyFor(individual).getStaticPotential();
        final double statPotlDiff = staticPotential.getPotential(referenceCell) - staticPotential.getPotential(targetCell);
        return statPotlDiff;
    }

    @Override
    public double updatePreferredSpeed(Individual individual) {
        return 0;
    }

    @Override
    public double updateExhaustion(Individual individual, EvacCell targetCell) {
        return 0;
    }

    @Override
    public double updatePanic(Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells) {
        return 0;
    }

    @Override
    public double idleThreshold(Individual i) {
        return i.getSlackness() * 0.4;
    }

    @Override
    public double changePotentialThreshold(Individual individual) {
        return 0;
    }
}
