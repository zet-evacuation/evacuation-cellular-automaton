package org.zet.cellularautomaton.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import org.zetool.algorithm.simulation.cellularautomaton.AbstractCellularAutomatonSimulationAlgorithm;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import org.zet.algo.ca.util.IndividualDistanceComparator;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;

/**
 * An implementation of a general cellular automaton algorithm specialized for evacuation simulation. The cells of the
 * cellular automaton are populized by {@link Individual}s and the simulation is rulebased performed only on these
 * populated cells. The algorithm is itself abstract and implementations have to specify the order in which the rules
 * are executed for the populating individuals.
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonAlgorithm
        extends AbstractCellularAutomatonSimulationAlgorithm<EvacuationCellularAutomatonInterface, EvacCell, EvacuationSimulationProblem, EvacuationSimulationResult> {

    /** The order in which the individuals are asked for. */
    public static final Function<List<Individual>,Iterator<Individual>> DEFAULT_ORDER = x -> x.iterator();
    /** The distance comparator. */
    private static final IndividualDistanceComparator DISTANCE_COMPARATOR = new IndividualDistanceComparator<>();
    /** Sorts the individuals by increasing distance to the exit. */
    public static final Function<List<Individual>, Iterator<Individual>> FRONT_TO_BACK = (List<Individual> t) -> {
        List<Individual> copy = new ArrayList<>(t);
        Collections.sort(copy, DISTANCE_COMPARATOR);
        return copy.iterator();
    };
    /** Sorts the individuals by decreasing distance to the exit. */
    public static final Function<List<Individual>, Iterator<Individual>> BACK_TO_FRONT = (List<Individual> t) -> {
        List<Individual> copy = new ArrayList<>(t);
        Collections.sort(copy, DISTANCE_COMPARATOR);
        Collections.reverse(copy);
        return copy.iterator();
    };
    /** The ordering used in the evacuation cellular automaton. */
    private Function<List<Individual>,Iterator<Individual>> reorder;
    
    public EvacuationCellularAutomatonAlgorithm() {
        this(DEFAULT_ORDER);
    }

    public EvacuationCellularAutomatonAlgorithm(Function<List<Individual>,Iterator<Individual>> reorder) {
        this.reorder = reorder;
    }

    @Override
    protected void initialize() {
        setMaxSteps(getProblem().getEvacuationStepLimit());
        log.log(Level.INFO, "{0} is executed. ", toString());

        getProblem().getCellularAutomaton().start();
        Individual[] individualsCopy = getProblem().getCellularAutomaton().getIndividuals().toArray(
                new Individual[getProblem().getCellularAutomaton().getIndividuals().size()]);
        for (Individual i : individualsCopy) {
            Iterator<EvacuationRule> primary = getProblem().getRuleSet().primaryIterator();
            EvacCell c = i.getCell();
            while (primary.hasNext()) {
                EvacuationRule r = primary.next();
                r.execute(c);
            }
        }
        getProblem().getCellularAutomaton().removeMarkedIndividuals();
    }

    @Override
    protected void performStep() {
        super.performStep();
        super.increaseStep();

        getProblem().getCellularAutomaton().removeMarkedIndividuals();
        getProblem().getPotentialController().updateDynamicPotential(
        getProblem().getParameterSet().probabilityDynamicIncrease(),
        getProblem().getParameterSet().probabilityDynamicDecrease());
        getProblem().getCellularAutomaton().nextTimeStep();

        fireProgressEvent(getProgress(), String.format("%1$s von %2$s individuals evacuated.",
                getProblem().getCellularAutomaton().getInitialIndividualCount() - getProblem().getCellularAutomaton().getIndividualCount(),
                getProblem().getCellularAutomaton().getInitialIndividualCount()));
    }

    @Override
    protected final void execute(EvacCell cell) {

        Individual i = Objects.requireNonNull(cell.getState().getIndividual(),
                "Execute called on EvacCell that does not contain an individual!");
        Iterator<EvacuationRule> loop = getProblem().getRuleSet().loopIterator();
        while (loop.hasNext()) { // Execute all rules
            EvacuationRule r = loop.next();
            r.execute(i.getCell());
        }
    }

    @Override
    protected EvacuationSimulationResult terminate() {
        // let die all individuals which are not already dead and not safe
        if (getProblem().getCellularAutomaton().getNotSafeIndividualsCount() != 0) {
            Individual[] individualsCopy = getProblem().getCellularAutomaton().getIndividuals().toArray(
                    new Individual[getProblem().getCellularAutomaton().getIndividuals().size()]);
            for (Individual i : individualsCopy) {
                if (!i.getCell().getState().getIndividual().isSafe()) {
                    getProblem().getCellularAutomaton().setIndividualDead(i, DeathCause.NOT_ENOUGH_TIME);
                }
            }
        }
        fireProgressEvent(1, "Simulation complete.");

        getProblem().getCellularAutomaton().stop();
        log("Time steps: " + getProblem().getCellularAutomaton().getTimeStep());
        return new EvacuationSimulationResult(getProblem().getCellularAutomaton().getTimeStep());
    }

    @Override
    protected boolean isFinished() {
        boolean thisFinished = allIndividualsSave() && timeOver();
        return super.isFinished() || thisFinished;
    }
    
    private boolean allIndividualsSave() {
        return getProblem().getCellularAutomaton().getNotSafeIndividualsCount() == 0;
    }
    
    private boolean timeOver() {
        return getProblem().getCellularAutomaton().getTimeStep() > getProblem().getCellularAutomaton().getNeededTime();
    }

    /**
     * Sends a progress event. The progress is defined as the maximum of the percentage of already evacuated individuals
     * and the fraction of time steps of the maximum amount of time steps already simulated.
     *
     * @return the current progress as percentage of safe individuals
     */
    @Override
    protected final double getProgress() {
        double timeProgress = super.getProgress();
        double individualProgress = 1.0 - ((double) getProblem().getCellularAutomaton().getIndividualCount()
                / getProblem().getCellularAutomaton().getInitialIndividualCount());
        return Math.max(individualProgress, timeProgress);
    }
    
    /**
     * An iterator that iterates over all cells of the cellular automaton that contains an individual. The rules of the
     * simulation algorithm are being executed on each of the occupied cells.
     *
     * @return iterator of all occupied cells
     */
    @Override
    public final Iterator<EvacCell> iterator() {
        return new CellIterator(reorder.apply(getProblem().getCellularAutomaton().getIndividuals()));
    }

    /**
     * A simple iterator that iterates over all cells of the cellular automaton that contain an individual. The
     * iteration order equals the order of the individuals given.
     */
    private static class CellIterator implements Iterator<EvacCell> {

        private final Iterator<Individual> individuals;

        /**
         * Initializes the object with a list of individuals whose cells are iterated over.
         *
         * @param individuals the individuals
         */
        private CellIterator(Iterator<Individual> individuals) {
            this.individuals = Objects.requireNonNull(individuals, "Individuals list must not be null.");
        }

        @Override
        public boolean hasNext() {
            return individuals.hasNext();
        }

        @Override
        public EvacCell next() {
            return individuals.next().getCell();
        }

        @Override
        public void remove() {
            throw new AssertionError("Attempted cell removal.");
        }
    }
}
