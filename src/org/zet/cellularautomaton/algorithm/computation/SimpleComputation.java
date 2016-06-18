package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
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

    protected PropertyAccess es;

    @Override
    public double changePotentialThreshold(Individual individual) {
        return 0;
    }

    /**
     *
     * @param referenceCell
     * @param targetCell
     * @param dynamicPotential
     * @return the potential difference between the two cells
     */
    @Override
    public double effectivePotential(EvacCell referenceCell, EvacCell targetCell, DynamicPotential dynamicPotential) {
        StaticPotential staticPotential = es.propertyFor(referenceCell.getState().getIndividual()).getStaticPotential();
        final double statPotlDiff = staticPotential.getPotential(referenceCell) - staticPotential.getPotential(targetCell);
        return statPotlDiff;
    }

    @Override
    public double movementThreshold(Individual i) {
        double individualSpeed = es.propertyFor(i).getRelativeSpeed();
        double cellSpeed = es.propertyFor(i).getCell().getSpeedFactor();
        return individualSpeed * cellSpeed;
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
    public double updatePreferredSpeed(Individual individual) {
        return 0;
    }

    @Override
    public double idleThreshold(Individual i) {
        return i.getSlackness() * 0.4;
    }

}
