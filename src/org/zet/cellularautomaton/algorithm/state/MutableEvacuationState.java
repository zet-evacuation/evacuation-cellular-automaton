package org.zet.cellularautomaton.algorithm.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MutableEvacuationState implements EvacuationState {

    private static final String ERROR_NOT_IN_LIST = "Specified individual is not in list individuals.";
    private final List<Individual> initialIndividuals = new LinkedList<>();
    private final List<Individual> remainingIndividuals = new LinkedList<>();
    private final Set<Individual> deadIndividuals = new HashSet<>();
    private final Set<Individual> safeIndividuals = new HashSet<>();
    /**
     * An ArrayList of all Individual objects, which are already out of the simulation because they are evacuated.
     */
    private final Set<Individual> evacuatedIndividuals = new HashSet<>();
    private int notSaveIndividualsCount = 0;

    public CAStatisticWriter caStatisticWriter;
    private final ParameterSet parameterSet;
    private final EvacuationCellularAutomatonInterface ca;
    private final Map<Individual, IndividualProperty> individualProperties;

    /** The minimal number of steps that is needed until all movements are FINISHED. */
    private int neededTime;
    int step = 0;
    /** An {@code ArrayList} marked to be removed. */
    private final List<Individual> markedForRemoval = new ArrayList<>();

    public MutableEvacuationState(ParameterSet parameterSet, EvacuationCellularAutomatonInterface ca,
            List<Individual> individuals) {
        this.parameterSet = parameterSet;
        this.ca = ca;
        individualProperties = new HashMap<>();
        for (Individual i : individuals) {
            addIndividualInt(i);
            individualProperties.put(i, new IndividualProperty(i));
        }
    }

    @Override
    public IndividualProperty propertyFor(Individual i) {
        return Objects.requireNonNull(individualProperties.get(i), "Individual " + i + " not in simulation.");
    }

    @Override
    public int getTimeStep() {
        return step;
    }

    public void increaseStep() {

    }

    @Override
    public void setNeededTime(int i) {
        neededTime = i;
    }

    @Override
    public int getNeededTime() {
        return neededTime;
    }

    @Override
    public CAStatisticWriter getStatisticWriter() {
        return caStatisticWriter;
    }

    @Override
    public void swapIndividuals(EvacCell cell1, EvacCell cell2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void moveIndividual(EvacCell from, EvacCell targetCell) {
        getCellularAutomaton().moveIndividual(from, targetCell);
    }

    @Override
    public void increaseDynamicPotential(EvacCell targetCell) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EvacuationCellularAutomatonInterface getCellularAutomaton() {
        return ca;
    }

    @Override
    public void markIndividualForRemoval(Individual i) {
        if (!getRemainingIndividuals().contains(i)) {
            throw new IllegalArgumentException("Individual " + i + " not in list!");
        }
        markedForRemoval.add(i);
    }

    @Override
    public void removeMarkedIndividuals() {
        markedForRemoval.stream().forEach(individual -> {
            setIndividualEvacuated(individual);
            getCellularAutomaton().setIndividualEvacuated(individual);
        });
        markedForRemoval.clear();
    }

    @Override
    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    /**
     * Returns the number of initialIndividuals that were in the cellular automaton when the simulation starts.
     *
     * @return the number of initialIndividuals
     */
    public int getInitialIndividualCount() {
        return initialIndividuals.size();
    }

    public void addIndividual(Individual i) {
        addIndividualInt(i);
    }
    private void addIndividualInt(Individual i) {
        if (initialIndividuals.contains(i)) {
            throw new IllegalArgumentException("Individual with id " + i.id() + " exists already in list individuals.");
        } else {
            initialIndividuals.add(i);
            remainingIndividuals.add(i);
        }

        notSaveIndividualsCount++;
    }

    /**
     * Returns the number of initialIndividuals that currently in the cellular automaton.
     *
     * @return the number of initialIndividuals in the cellular automaton
     */
    public int getRemainingIndividualCount() {
        return remainingIndividuals.size();
    }

    /**
     * Returns a list of all initialIndividuals that are active in the simulation. The list does not contain individuals
     * which are dead or evacuated. Safe initialIndividuals are contained in the list.
     *
     * @return list of active initialIndividuals
     */
    public List<Individual> getRemainingIndividuals() {
        return Collections.unmodifiableList(remainingIndividuals);
    }

    /**
     * Returns a view of all initialIndividuals.
     *
     * @return the view
     */
    public List<Individual> getInitialIndividuals() {
        return Collections.unmodifiableList(initialIndividuals);
    }

    @Override
    public Iterator<Individual> iterator() {
        return initialIndividuals.iterator();
    }

    /**
     * Indicates, if the individual is already safe; that means: on save or exit cells.
     */
    /**
     * Returns, if the individual is already safe; that means: on save- oder exit cells.
     *
     * @param i indicates wheather the individual is save or not
     * @return if the individual is already safe
     */
//    public boolean isSafe(Individual i) {
//        if (!(initialIndividuals.contains(i) || safeIndividuals.contains(i))) {
//            throw new IllegalArgumentException(ERROR_NOT_IN_LIST);
//        }
//        return safeIndividuals.contains(i);
//    }

    /**
     * Sets the safe-status of the individual.
     *
     * @param i indicates wheather the individual is save or not
     */
    public void setSafe(Individual i) {
        if (safeIndividuals.contains(i)) {
            return;
        }
        safeIndividuals.add(i);
        notSaveIndividualsCount--;
    }

    /**
     * Removes an individual from the list of all initialIndividuals of the building and adds it to the list of
     * initialIndividuals, which are out of the simulation because the are evacuated.
     *
     * @throws java.lang.IllegalArgumentException if the the specific individual does not exist in the list
     * initialIndividuals
     * @param i specifies the Individual object which has to be removed from the list and added to the other list
     */
    public void setIndividualEvacuated(Individual i) {
        if (!initialIndividuals.contains(i)) {
            throw new IllegalArgumentException(ERROR_NOT_IN_LIST);
        }
        setSafe(i);
        remainingIndividuals.remove(i);

        evacuatedIndividuals.add(i);
    }

    /**
     * Returns true, if the person is evacuated, false elsewise.
     *
     * @param i the individual
     * @return the evacuation status
     */
    public boolean isEvacuated(Individual i) {
        if (!(initialIndividuals.contains(i) || evacuatedIndividuals.contains(i))) {
            throw new IllegalArgumentException(ERROR_NOT_IN_LIST);
        }
        return evacuatedIndividuals.contains(i);
    }

    /**
     * Returns a view of all evacuated initialIndividuals.
     *
     * @return the view
     */
    public Collection<Individual> getEvacuatedIndividuals() {
        return Collections.unmodifiableSet(evacuatedIndividuals);
    }

    public int evacuatedIndividualsCount() {
        return evacuatedIndividuals.size();
    }

    public void die(Individual i) {
        if (!remainingIndividuals.remove(i)) {
            throw new IllegalArgumentException(ERROR_NOT_IN_LIST);
        }
        deadIndividuals.add(i);

        notSaveIndividualsCount--;
    }

    public int deadIndividualsCount() {
        return deadIndividuals.size();
    }

    /**
     * Returns a view of all dead initialIndividuals.
     *
     * @return the view
     */
    public Collection<Individual> getDeadIndividuals() {
        return Collections.unmodifiableSet(deadIndividuals);
    }

    public int getNotSafeIndividualsCount() {
        return notSaveIndividualsCount;
    }

    /**
     * Calculates the number of individuals that died by a specified death cause.
     *
     * @param deathCause the death cause
     * @return the number of individuals died by the death cause
     */
    public int getDeadIndividualCount(DeathCause deathCause) {
        int count = 0;
        count = getDeadIndividuals().stream().filter(i -> propertyFor(i).getDeathCause() == deathCause).
                map(_item -> 1).reduce(count, Integer::sum);
        return count;
    }

    int getStep() {
        return step;
    }

}
