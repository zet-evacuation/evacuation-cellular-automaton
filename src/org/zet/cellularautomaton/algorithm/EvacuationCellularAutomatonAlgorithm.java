package org.zet.cellularautomaton.algorithm;

import static org.zetool.common.util.Helper.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.algorithm.state.MutableEvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateController;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.statistic.results.StoredCAStatisticResults;
import org.zetool.algorithm.simulation.cellularautomaton.AbstractCellularAutomatonSimulationAlgorithm;

/**
 * An implementation of a general cellular automaton algorithm specialized for evacuation simulation. The cells of the
 * cellular automaton are populized by {@link Individual}s and the simulation is rulebased performed only on these
 * populated cells. The algorithm is itself abstract and implementations have to specify the order in which the rules
 * are executed for the populating individuals.
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonAlgorithm
        extends AbstractCellularAutomatonSimulationAlgorithm<EvacuationCellularAutomatonInterface, EvacCellInterface, EvacuationSimulationProblem, EvacuationSimulationResult> {

    /**
     * The order in which the individuals are asked for.
     */
    public static final Function<List<Individual>, Iterator<Individual>> DEFAULT_ORDER = x -> x.iterator();

    /**
     * The ordering used in the evacuation cellular automaton.
     */
    private Function<List<Individual>, Iterator<Individual>> reorder;
    protected MutableEvacuationState es = new MutableEvacuationState(new EvacuationCellularAutomaton(),
            Collections.emptyList());
    protected EvacuationStateController ec = null;

    public EvacuationCellularAutomatonAlgorithm() {
        this(DEFAULT_ORDER);
    }

    public EvacuationCellularAutomatonAlgorithm(Function<List<Individual>, Iterator<Individual>> reorder) {
        this.reorder = reorder;
    }

    @Override
    protected void initialize() {
        initRulesAndState();

        setMaxSteps(getProblem().getEvacuationStepLimit());
        log.log(Level.INFO, "{0} is executed. ", toString());

        Individual[] individualsCopy = es.getInitialIndividuals().toArray(
                new Individual[es.getInitialIndividuals().size()]);
        for (Individual i : individualsCopy) {
            Iterator<EvacuationRule> primary = getProblem().getRuleSet().primaryIterator();
            EvacCellInterface c = es.propertyFor(i).getCell();
            while (primary.hasNext()) {
                EvacuationRule r = primary.next();
                r.execute(c);
            }
        }
        es.removeMarkedIndividuals();
    }

    public void setNeededTime(int i) {
        es.setNecessaryTime(i);
    }

    private void initRulesAndState() {
        es = new MutableEvacuationState(getProblem().getCellularAutomaton(),
                getProblem().getIndividuals());
        EvacuationCellularAutomatonInterface eca = getProblem().getCellularAutomaton();
        for (Map.Entry<Individual, ? extends EvacCellInterface> e : getProblem().individualStartPositions().entrySet()) {
            es.propertyFor(e.getKey()).setCell(e.getValue());
            es.propertyFor(e.getKey()).setStaticPotential(eca.minPotentialFor(e.getValue()));
        }
        ec = new EvacuationStateController((MutableEvacuationState) es);
        for (EvacuationRule r : getProblem().getRuleSet()) {
            r.setEvacuationState(es);
            r.setEvacuationStateController(ec);
        }
    }

    @Override
    protected void performStep() {
        super.performStep();
        super.increaseStep();

        es.removeMarkedIndividuals();
        ec.updateDynamicPotential(getProblem().getParameterSet().probabilityDynamicIncrease(),
                getProblem().getParameterSet().probabilityDynamicDecrease());

        fireProgressEvent(getProgress(), String.format("%1$s von %2$s individuals evacuated.",
                es.getInitialIndividualCount() - es.getRemainingIndividualCount(),
                es.getInitialIndividualCount()));
    }

    @Override
    protected final void execute(EvacCellInterface cell) {
        Individual i = Objects.requireNonNull(cell.getState().getIndividual(),
                "Execute called on EvacCell that does not contain an individual!");
        for (EvacuationRule r : in(getProblem().getRuleSet().loopIterator())) {
            r.execute(es.propertyFor(i).getCell());
        }
    }

    @Override
    protected EvacuationSimulationResult terminate() {
        // let die all individuals which are not already dead and not safe
        if (es.getNotSafeIndividualsCount() != 0) {
            Individual[] individualsCopy = es.getRemainingIndividuals().toArray(
                    new Individual[es.getRemainingIndividuals().size()]);
            for (Individual i : individualsCopy) {
                if (!es.propertyFor(i).isSafe()) {
                    ec.die(i, DeathCause.NOT_ENOUGH_TIME);
                }
            }
        }
        fireProgressEvent(1, "Simulation complete.");

        EvacuationSimulationProblem p = getProblem();
        log("Time steps: " + getStep());
        return new EvacuationSimulationResult(getStep());
    }

    public StoredCAStatisticResults getStatisticResults() {
        return es.getStatisticWriter().getStoredCAStatisticResults();
    }

    @Override
    protected boolean isFinished() {
        boolean thisFinished = allIndividualsSave() && timeOver();
        return super.isFinished() || thisFinished;
    }

    private boolean allIndividualsSave() {
        return es.getNotSafeIndividualsCount() == 0;
    }

    private boolean timeOver() {
        return getStep() > es.getNecessaryTime();
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
        double individualProgress = 1.0 - ((double) es.getRemainingIndividualCount()
                / getProblem().getIndividuals().size());
        return Math.max(individualProgress, timeProgress);
    }

    /**
     * An iterator that iterates over all cells of the cellular automaton that contains an individual. The rules of the
     * simulation algorithm are being executed on each of the occupied cells.
     *
     * @return iterator of all occupied cells
     */
    @Override
    public final Iterator<EvacCellInterface> iterator() {
        return new CellIterator(reorder.apply(es.getRemainingIndividuals()), es);
    }

    /**
     * A simple iterator that iterates over all cells of the cellular automaton that contain an individual. The
     * iteration order equals the order of the individuals given.
     */
    private static class CellIterator implements Iterator<EvacCellInterface> {

        private final Iterator<Individual> individuals;
        private final EvacuationState es;

        /**
         * Initializes the object with a list of individuals whose cells are iterated over.
         *
         * @param individuals the individuals
         */
        private CellIterator(Iterator<Individual> individuals, EvacuationState es) {
            this.individuals = Objects.requireNonNull(individuals, "Individuals list must not be null.");
            this.es = es;
        }

        @Override
        public boolean hasNext() {
            return individuals.hasNext();
        }

        @Override
        public EvacCellInterface next() {
            return es.propertyFor(individuals.next()).getCell();
        }

        @Override
        public void remove() {
            throw new AssertionError("Attempted cell removal.");
        }
    }

    public EvacuationState getEvacuationState() {
        return es;
    }
    
    protected EvacuationStateControllerInterface getEvacuationController() {
        return ec;
    }

    /**
     * Iterates the individuals by increasing distance to the exit.
     *
     * @return returns a instance of the algorithm
     */
    public static EvacuationCellularAutomatonAlgorithm getFrontToBackAlgorithm() {
        IndividualDistanceComparator comparator = new IndividualDistanceComparator();
        EvacuationCellularAutomatonAlgorithm algo = new EvacuationCellularAutomatonAlgorithm(getFrontToBack(comparator));
        comparator.algorithm = algo;
        return algo;
    }

    private static Function<List<Individual>, Iterator<Individual>> getFrontToBack(IndividualDistanceComparator c) {
        return (List<Individual> t) -> {
            List<Individual> copy = new ArrayList<>(t);
            Collections.sort(copy, c);
            return copy.iterator();
        };
    }

    /**
     * Iterates the individuals by decreasing distance to the exit.
     *
     * @return returns an instance of the algorithm
     */
    public static EvacuationCellularAutomatonAlgorithm getBackToFrontAlgorithm() {
        IndividualDistanceComparator comparator = new IndividualDistanceComparator();
        EvacuationCellularAutomatonAlgorithm algo = new EvacuationCellularAutomatonAlgorithm(getBackToFront(comparator));
        comparator.algorithm = algo;
        return algo;
    }

    private static Function<List<Individual>, Iterator<Individual>> getBackToFront(IndividualDistanceComparator c) {
        return (List<Individual> t) -> {
            List<Individual> copy = new ArrayList<>(t);
            Collections.sort(copy, c);
            Collections.reverse(copy);
            return copy.iterator();
        };
    }

    /**
     * The class {@code IndividualDistanceComparator} compares two individuals in means of their distance to the exit
     * using their currently selected potential field.
     *
     * @param <E> the compared object class, that must extend {@link ds.ca.Individual}
     * @author Jan-Philipp Kappmeier
     */
    private static class IndividualDistanceComparator<E extends Individual> implements Comparator<E> {

        EvacuationCellularAutomatonAlgorithm algorithm;

        /**
         * Creates a new instance of {@code IndividualDistanceComparator}. No initialization is needed.
         */
        public IndividualDistanceComparator() {

        }

        /**
         * Compares two individuals in means of the distance. The distance is the value of the potantial of the cell on
         * which the individual stands.
         *
         * An example:
         *
         * Individual 1 has a distance of 20 and individual 2 has a distance of 100. Then individual 1 is nearer to the
         * exit than individual 2. The returned value is (20 - 100) and thus negative.
         *
         * @param i1 the first individual
         * @param i2 the second individual
         * @return the difference in distance between the two individauls
         */
        @Override
        public int compare(Individual i1, Individual i2) {
            EvacuationState es = algorithm.es;
            return es.propertyFor(i1).getStaticPotential().getPotential(es.propertyFor(i1).getCell())
                    - es.propertyFor(i2).getStaticPotential().getPotential(es.propertyFor(i2).getCell());
        }

        /**
         * Returns the name of the class.
         *
         * @return the name of the class
         */
        @Override
        public String toString() {
            return "IndividualDistanceComparator";
        }
    }
}
