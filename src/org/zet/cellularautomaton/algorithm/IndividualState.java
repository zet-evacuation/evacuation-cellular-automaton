package org.zet.cellularautomaton.algorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.zet.cellularautomaton.Individual;

/**
 * Collects the state of each individual in the simulation.
 *
 * @author Jan-Philipp Kappmeier
 */
public class IndividualState implements Iterable<Individual> {

    private static final String ERROR_NOT_IN_LIST = "Specified individual is not in list individuals.";

    private final List<Individual> initialIndividuals = new LinkedList<>();
    private final List<Individual> remainingIndividuals = new LinkedList<>();
    private final Set<Individual> deadIndividuals = new HashSet<>();
    private final Set<Individual> safeIndividuals = new HashSet<>();
    /** An ArrayList of all Individual objects, which are already out of the simulation because they are evacuated. */
    private final Set<Individual> evacuatedIndividuals = new HashSet<>();
    //private final Map<Individual, DeathCause> deathCause = new HashMap<>();
    private int notSaveIndividualsCount = 0;
    private int initialIndividualCount;

    /**
     * Returns the number of initialIndividuals that were in the cellular automaton when the simulation starts.
     *
     * @return the number of initialIndividuals
     */
    public int getInitialIndividualCount() {
        return initialIndividualCount;
    }

    protected void addIndividual(Individual i) {
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
     * Returns a list of all initialIndividuals that are active in the simulation. The list does not contain
     * individuals which are dead or evacuated. Safe initialIndividuals are contained in the list.
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
    public boolean isSafe(Individual i) {
        if (!(initialIndividuals.contains(i) || safeIndividuals.contains(i))) {
            throw new IllegalArgumentException(ERROR_NOT_IN_LIST);
        }
        return safeIndividuals.contains(i);
    }

    /**
     * Sets the safe-status of the individual.
     *
     * @param i indicates wheather the individual is save or not
     */
    public void setSafe(Individual i) {
        if( isSafe(i)) {
            return;
        }
        safeIndividuals.add(i);
        notSaveIndividualsCount--;
    }

    /**
     * Removes an individual from the list of all initialIndividuals of the building and adds it to the list of initialIndividuals,
 which are out of the simulation because the are evacuated.
     *
     * @throws java.lang.IllegalArgumentException if the the specific individual does not exist in the list initialIndividuals
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

}
