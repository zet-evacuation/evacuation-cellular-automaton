package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
import java.util.List;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;
import org.zet.cellularautomaton.potential.DynamicPotential;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.rndutils.RandomUtils;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultComputation implements Computation {

    protected PropertyAccess es;
    protected ParameterSet parameterSet;
    final private double MINIMUM_PANIC = 0.0d;
    final private double MAXIMUM_PANIC = 1.0d;

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

    /*
     * {@inheritDoc}
     * @see algo.ca.parameter.AbstractParameterSet#movementThreshold( ds.ca.Individual )
     */
    // wird nur benutzt wenn die Geschwindigkeit der Individuen mit Wahrscheinlichkeiten simuliert wird
    // das ist in der NonWaitingMovementRule nicht der Fall
    @Override
    public double movementThreshold(Individual i) {
        double individualSpeed = es.propertyFor(i).getRelativeSpeed();
        double cellSpeed = es.propertyFor(i).getCell().getSpeedFactor();
        // double exhaustion = es.propertyFor(i).getExhaustion();  brauchen wir nur wenn wir in
        //currentspeed exhaustion nicht einrechenen
        return individualSpeed * cellSpeed;
    }

    /**
     * <p>
     * Given a cell {@code referenceCell} that is occupied by an individual I, this method calculates the potential of a
     * cell with respect to I's panic and both the static and the dynamic potential. One can think of the resulting
     * potential as an "average" of the static and the dynamic potential. However, the influence of the static and the
     * dynamic potential on the average is determined by two constants and I's panic. The higher the panic, the more
     * important the dynamic potential will become while the influence of the static potential lessens.</p>
     *
     * @param referenceCell A cell with an individual
     * @param targetCell A neighbour of {@code cell}
     * @param dynamicPotential the dynamic potential
     * @return The potential between {@code referenceCell} and {@code targetCell} with respect to the static and the
     * dynamic potential.
     */
    @Override
    public double effectivePotential(EvacCell referenceCell, EvacCell targetCell, DynamicPotential dynamicPotential) {
        if (referenceCell.getState().isEmpty()) {
            throw new IllegalArgumentException(CellularAutomatonLocalization.LOC.getString("algo.ca.parameter.NoIndividualOnReferenceCellException"));
        }
        final double panic = es.propertyFor(referenceCell.getState().getIndividual()).getPanic();
        StaticPotential staticPotential = es.propertyFor(referenceCell.getState().getIndividual()).getStaticPotential();

        if (dynamicPotential != null) {
            final double dynPotDiff = (-1) * (dynamicPotential.getPotential(referenceCell) - dynamicPotential.getPotential(targetCell));
            final double statPotlDiff = staticPotential.getPotential(referenceCell) - staticPotential.getPotential(targetCell);
            return (Math.pow(panic, parameterSet.PANIC_WEIGHT_ON_POTENTIALS()) * dynPotDiff * parameterSet.dynamicPotentialWeight()) + ((1 - Math.pow(panic, parameterSet.PANIC_WEIGHT_ON_POTENTIALS())) * statPotlDiff * parameterSet.staticPotentialWeight());
            //return statPotlDiff * staticPotentialWeight();
        } else {
            //    System.out.println( "DynamicPotential = NULL!");
            final double statPotlDiff = staticPotential.getPotential(referenceCell) - staticPotential.getPotential(targetCell);
            return Math.pow(1 - panic, parameterSet.PANIC_WEIGHT_ON_POTENTIALS()) * statPotlDiff * parameterSet.staticPotentialWeight();
        }
    }

    /*
     * {@inheritDoc}
     * @see algo.ca.parameter.AbstractParameterSet#updateExhaustion(ds.ca.Individual)
     */
    @Override
    public double updateExhaustion(Individual individual, EvacCell targetCell) {
        // ExhaustionFactor depends from the age. currently it is always initialized with 1, so all individuals exhauste with the same
        // speed.
        // i hope the formular is right: it does the following
        // each individual looses a percentage of the exhauston factor, depending of the current speed:
        // currentSpeed / maxSpeed. this is a value between 0 and 1
        // each individual has an exhaustionfactor between 0 and 1 that describes the speed with which the exhaustion decreases.
        // the resulting value is a value between 0 and 1 and increases the exhaustion, so it is added to the old exhaustion value.
        final double MIN_EXHAUSTION = 0d;
        final double MAX_EXHAUSTION = 0.99d;
        double newExhaustion;
        if (es.propertyFor(individual).getCell().equals(targetCell)) {
            newExhaustion = (0 / individual.getMaxSpeed() - 0.5) * individual.getExhaustionFactor()
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


    /*
     * {@inheritDoc}
     * @see algo.ca.parameter.AbstractParameterSet#updateSpeed(ds.ca.Individual)
     */
    @Override
    public double updatePreferredSpeed(Individual i) {
        //double oldSpeed = es.propertyFor(i).getRelativeSpeed();
        double maxSpeed = i.getMaxSpeed();
        double newSpeed = maxSpeed + ((es.propertyFor(i).getPanic() * parameterSet.panicWeightOnSpeed()) - (es.propertyFor(i).getExhaustion() * parameterSet.exhaustionWeightOnSpeed()));
        es.propertyFor(i).setRelativeSpeed(Math.max(0.0001, Math.min(maxSpeed, newSpeed)));

        //        if( es.propertyFor(i).getMaxSpeed() < newSpeed )
//            i.setRelativeSpeed( es.propertyFor(i).getMaxSpeed() );
//        else
//            i.setRelativeSpeed( newSpeed );
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
            double potentialDifference = es.propertyFor(individual).getStaticPotential().getPotential(es.propertyFor(individual).getCell()) - es.propertyFor(individual).getStaticPotential().getPotential(cell);
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
            newPanic = newPanic - individual.getPanicFactor() * parameterSet.getPanicDecrease() * (parameterSet.PANIC_THRESHOLD() - failures);
        } else {
            newPanic = newPanic + individual.getPanicFactor() * (failures - parameterSet.PANIC_THRESHOLD()) * parameterSet.getPanicIncrease();
        }

        newPanic = Math.max(MINIMUM_PANIC, newPanic);
        newPanic = Math.min(MAXIMUM_PANIC, newPanic);

        es.propertyFor(individual).setPanic(newPanic);
        return newPanic;

        /* alter Code */
//            // update panic only if the individual is not standing on a savecell or an exitcell
//            if (! ( (es.propertyFor(individual).getCell() instanceof ds.ca.SaveCell) || (es.propertyFor(individual).getCell() instanceof ds.ca.ExitCell) )) {
//
//                double panic = es.propertyFor(individual).getPanic();
//
//                //person will gar nicht laufen (slack usw.)
//                if( preferedCells.size() == 0 )
//                    return panic;
//
//                Iterator<Cell> it = preferedCells.iterator();
//                EvacCell neighbour = it.next();
//                double panicFactor = es.propertyFor(individual).getPanicFactor();
//                if(es.propertyFor(individual).getCell() != targetCell){
//                    individual.setPanic(Math.max(panic - getPanicDecrease()*0.17, MINIMUM_PANIC));
//                    return es.propertyFor(individual).getPanic();
//                }
//
//                int skippedCells = 0;
//                while( it.hasNext() && neighbour != targetCell ) {
//                    if( neighbour.getIndividual() != null ) {
//                        panic += getPanicIncrease() * panicFactor / (2 << (preferedCells.size() - skippedCells));
//                        skippedCells++;
//                    }
//                    neighbour = it.next();
//                }
//
//                individual.setPanic( Math.min(panic, MAXIMUM_PANIC));
//                }
//                return es.propertyFor(individual).getPanic();
            /* Ende alter Code */
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
