package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.math.averaging.NonLinearAverages;
import org.zetool.rndutils.RandomUtils;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultComputation implements Computation {

    protected final PropertyAccess es;
    protected final ParameterSet parameterSet;
    private static final double MINIMUM_PANIC = 0.0d;
    private static final double MAXIMUM_PANIC = 1.0d;
    private static final double MIN_EXHAUSTION = 0d;
    private static final double MAX_EXHAUSTION = 0.999d;

    public DefaultComputation(PropertyAccess es, ParameterSet parameterSet) {
        this.es = es;
        this.parameterSet = parameterSet;
    }

    /**
     * {@inheritDoc}
     *
     * @param i
     * @return the individual threshold for the individual {@code i}
     * @see algo.ca.parameter.AbstractDefaultParameterSet#changePotentialThreshold(ds.ca.Individual)
     */
    @Override
    public double changePotentialThreshold(Individual i) {
        return es.propertyFor(i).getPanic() * parameterSet.panicToProbOfPotentialChangeRatio();
    }

    /**
     * Given a cell {@literal referenceCell} that is occupied by an individual {@literal I}, this method calculates the
     * potential of a cell with respect to {@literal I}'s panic and both the static and the dynamic potential. One can
     * think of the resulting potential as an "average" of the static and the dynamic potential. However, the influence
     * of the static and the dynamic potential on the average is determined by two constants and I's panic. The higher
     * the panic, the more important the dynamic potential will become while the influence of the static potential
     * lessens.
     *
     * @param individual A cell with an individual
     * @param targetCell A neighbour of {@code cell}
     * @param dynamicPotential the dynamic potential
     * @return The potential between {@code referenceCell} and {@code targetCell} with respect to the static and the
     * dynamic potential.
     */
    @Override
    public double effectivePotential(Individual individual, EvacCell targetCell,
            Function<EvacCell,Double> dynamicPotential) {
        EvacCell referenceCell = es.propertyFor(individual).getCell();
        assert !referenceCell.getState().isEmpty();

        final double panic = es.propertyFor(referenceCell.getState().getIndividual()).getPanic();
        StaticPotential staticPotential = es.propertyFor(referenceCell.getState().getIndividual()).getStaticPotential();

        if (dynamicPotential != null) {
            final double dynPotDiff = dynamicPotential.apply(referenceCell)
                    - dynamicPotential.apply(targetCell);
            final double statPotlDiff = (double)staticPotential.getPotential(referenceCell)
                    - staticPotential.getPotential(targetCell);
            return NonLinearAverages.logisticAverage(panic, statPotlDiff * parameterSet.staticPotentialWeight(),
                    dynPotDiff * parameterSet.dynamicPotentialWeight());
        } else {
            final double statPotlDiff = (double)staticPotential.getPotential(referenceCell)
                    - staticPotential.getPotential(targetCell);
            return statPotlDiff * parameterSet.staticPotentialWeight();
        }
    }

    /**
     * {@inheritDoc}
     * @see algo.ca.parameter.AbstractParameterSet#updateExhaustion(ds.ca.Individual)
     */
    @Override
    public double updateExhaustion(Individual individual, EvacCell targetCell) {
        // ExhaustionFactor depends from the age. currently it is always initialized with 1, so all individuals exhauste
        // with the same speed.
        // i hope the formular is right: it does the following
        // each individual looses a percentage of the exhauston factor, depending of the current speed:
        // currentSpeed / maxSpeed. this is a value between 0 and 1
        // each individual has an exhaustionfactor between 0 and 1 that describes the speed with which the exhaustion decreases.
        // the resulting value is a value between 0 and 1 and increases the exhaustion, so it is added to the old exhaustion value.
        double newExhaustion;
        
        if (es.propertyFor(individual).getCell().equals(targetCell)) {
            newExhaustion = -0.5 * individual.getExhaustionFactor()
                    + es.propertyFor(individual).getExhaustion();
        } else {
            newExhaustion = (es.propertyFor(individual).getRelativeSpeed() / individual.getMaxSpeed() - 0.5)
                    * individual.getExhaustionFactor() + es.propertyFor(individual).getExhaustion();
        }

        if (newExhaustion < MIN_EXHAUSTION) {
            newExhaustion = MIN_EXHAUSTION;
        } else if (newExhaustion > MAX_EXHAUSTION) {
            newExhaustion = MAX_EXHAUSTION;
        }
        
        es.propertyFor(individual).setExhaustion(newExhaustion);

        return newExhaustion;
    }

    /**
     * {@inheritDoc}
     * @see algo.ca.parameter.AbstractParameterSet#updateSpeed(ds.ca.Individual)
     */
    @Override
    public double updatePreferredSpeed(Individual i) {
        double maxSpeed = i.getMaxSpeed();
        double newSpeed = maxSpeed + ((es.propertyFor(i).getPanic() * parameterSet.panicWeightOnSpeed())
                - (es.propertyFor(i).getExhaustion() * parameterSet.exhaustionWeightOnSpeed()));
        es.propertyFor(i).setRelativeSpeed(Math.max(0.0001, Math.min(maxSpeed, newSpeed)));

        return es.propertyFor(i).getRelativeSpeed();
    }

    @Override
    public double updatePanic(Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells) {
        List<EvacCell> possibleNeighbours = es.propertyFor(individual).getCell().getNeighbours();
        if (possibleNeighbours.isEmpty()) {
            return es.propertyFor(individual).getPanic();
        }

        double[] potentials = new double[possibleNeighbours.size()];
        int idx = 0;
        for (EvacCell cell : possibleNeighbours) {
            double potentialDifference = (double)es.propertyFor(individual).getStaticPotential().getPotential(
                    es.propertyFor(individual).getCell())
                    - es.propertyFor(individual).getStaticPotential().getPotential(cell);
            potentials[idx] = Math.exp(potentialDifference);
            idx++;
        }

        int failures = 0;

        int chosenNeighbour = RandomUtils.getInstance().chooseRandomlyAbsolute(potentials);
        while (!possibleNeighbours.get(chosenNeighbour).getState().isEmpty() && failures <= possibleNeighbours.size()) {
            failures++;
            potentials[chosenNeighbour] = 0;
            chosenNeighbour = RandomUtils.getInstance().chooseRandomlyAbsolute(potentials);
        }

        double newPanic = es.propertyFor(individual).getPanic();
        if (failures < parameterSet.PANIC_THRESHOLD()) {
            newPanic = newPanic - individual.getPanicFactor() * parameterSet.getPanicDecrease()
                    * (parameterSet.PANIC_THRESHOLD() - failures);
        } else {
            newPanic = newPanic + individual.getPanicFactor() * (failures - parameterSet.PANIC_THRESHOLD())
                    * parameterSet.getPanicIncrease();
        }

        newPanic = Math.max(MINIMUM_PANIC, newPanic);
        newPanic = Math.min(MAXIMUM_PANIC, newPanic);

        es.propertyFor(individual).setPanic(newPanic);
        return newPanic;
    }

    /*
     * {@inheritDoc}
     * @see algo.ca.parameter.AbstractParameterSet#idleThreshold(ds.ca.Individual)
     */

    @Override
    public double idleThreshold(Individual i) {
        return i.getSlackness() * parameterSet.slacknessToIdleRatio();
    }

}
