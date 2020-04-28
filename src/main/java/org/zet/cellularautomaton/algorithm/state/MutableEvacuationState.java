/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.algorithm.state;

import java.text.MessageFormat;
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
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualToExitMapping;
import org.zet.cellularautomaton.potential.DynamicPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;

/**
 * The current state of the simulation run.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class MutableEvacuationState implements EvacuationState {

    /** Generic error message. */
    private static final MessageFormat ERROR_NOT_EXISTING = new MessageFormat("Individual {0} not in simulation.");
    private static final MessageFormat ERROR_NOT_IN_LIST = new MessageFormat("Individual {0} not in list!");
    private static final MessageFormat ERROR_NOT_DEAD = new MessageFormat("Individual {0} not dead.");
    private static final MessageFormat ERROR_NOT_SAFE = new MessageFormat("Individual {0} not safe.");
    private static final MessageFormat ERROR_NOT_EVACUATED = new MessageFormat("Individual {0} not evacuated.");
    
    /** The cellular automaton of the simulation run. */
    private final EvacuationCellularAutomaton ca;
    /** The parameter set of the simulation run. */

    /** Mapping of individuals to their dynamic properties. */
    private final Map<Individual, IndividualProperty> individualProperties;
    
    /** The individuals initially in the simulation. */
    private final List<Individual> initialIndividuals = new LinkedList<>();
    /** The individuals that are still active in the simulation, i.e. living, unsafe. */
    private final List<Individual> remainingIndividuals = new LinkedList<>();
    /** The individuals that are already dead. */
    private final Set<Individual> deadIndividuals = new HashSet<>();
    /** The individuals that are safe, but not necessarily evacuated. */
    private final Set<Individual> safeIndividuals = new HashSet<>();
    /** The individuals which are already out of the simulation because they are evacuated. */
    private final Set<Individual> evacuatedIndividuals = new HashSet<>();
    /** The number of individuals that are active but not yet safe. */
    private int notSaveIndividualsCount = 0;

    /** The minimal number of steps that is needed until all movements are FINISHED. */
    private int necessaryTime;
    /** The current simulation step. */
    int currentStep = 0;
    /** The individuals that are to be removed at the end of the current step. */
    private final List<Individual> markedForRemoval = new ArrayList<>();

    /** The single DynamicPotential. */
    private final DynamicPotential dynamicPotential;

    /** A mapping that maps individuals to exits. */
    private IndividualToExitMapping individualToExitMapping;

    /** Statistics writer. TODO: remove */
    public CAStatisticWriter caStatisticWriter;

    public MutableEvacuationState(EvacuationCellularAutomaton ca, List<Individual> individuals) {
        this.ca = ca;
        individualProperties = new HashMap<>();
        individuals.stream().forEach(individual -> addIndividualInt(individual));
        dynamicPotential = new DynamicPotential();
        caStatisticWriter = new CAStatisticWriter(this);
        }
    
    @Override
    public IndividualProperty propertyFor(Individual i) {
        return Objects.requireNonNull(individualProperties.get(i), () -> ERROR_NOT_EXISTING.format(new Object[] {i}));
    }

    @Override
    public int getTimeStep() {
        return currentStep;
    }

    public void increaseStep() {
        currentStep++;
        updateNecessaryTime();
    }
    
    private void updateNecessaryTime() {
        double tempTime = necessaryTime;
        for( Individual i : remainingIndividuals ) {
            tempTime = Math.max(necessaryTime, propertyFor(i).getStepEndTime());
        }
        necessaryTime = (int)Math.ceil(tempTime);        
    }

    /**
     * Sets a new minimal number of time steps that are necessary to finish all (virtually non discrete) movements.
     *
     * @param necessaryTime the time step until which the simulation has to continue at least
     */
    public void setNecessaryTime(int necessaryTime) {
        this.necessaryTime = necessaryTime;
    }

    @Override
    public int getNecessaryTime() {
        return necessaryTime;
    }

    @Override
    public CAStatisticWriter getStatisticWriter() {
        return caStatisticWriter;
    }


    /**
     * Get the dynamicPotential. Returns null if the DynamicPotential not exists.
     *
     * @param cell the evacuation cell
     * @return the dynamic potential value of the cell
     */
    @Override
    public double getDynamicPotential(EvacCellInterface cell) {
        return dynamicPotential.getPotential(cell);
    }

    void increaseDynamicPotential(EvacCellInterface targetCell) {
        dynamicPotential.increase(targetCell);
    }
    public void setDynamicPotential(EvacCell cell, double value) {
        dynamicPotential.setPotential(cell, value);
    }

    void updateDynamicPotential(double probabilityDynamicIncrease, double probabilityDynamicDecrease) {
        dynamicPotential.update(probabilityDynamicIncrease, probabilityDynamicDecrease);
    }

    @Override
    public EvacuationCellularAutomaton getCellularAutomaton() {
        return ca;
    }

//    @Override
//    public void markIndividualForRemoval(Individual i) {
//        if (!getRemainingIndividuals().contains(i)) {
//            throw new IllegalArgumentException(ERROR_NOT_IN_LIST.format(new Object[] {i}));
//        }
//        markedForRemoval.add(i);
//    }

//    @Override
//    public void removeMarkedIndividuals() {
//        markedForRemoval.stream().forEach(individual -> {
//            if(!propertyFor(individual).isEvacuated()) {
//                propertyFor(individual).setEvacuationTime(currentStep);
//            }
//            addToEvacuated(individual);
//        });
//        markedForRemoval.clear();
//    }

    /**
     * Returns the number of initialIndividuals that were in the cellular automaton when the simulation starts.
     *
     * @return the number of initialIndividuals
     */
    @Override
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
            individualProperties.put(i, new IndividualProperty(i));
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
    @Override
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
     * Adds a saved individual to the list of safe individuals.
     *
     * @param i indicates wheather the individual is save or not
     */
    public void addToSafe(Individual i) {
        if (safeIndividuals.contains(i)) {
            return;
        }

        if(!propertyFor(i).isSafe()) {
            throw new IllegalArgumentException(ERROR_NOT_SAFE.format(new Object[] {i}));
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
    public void addToEvacuated(Individual i) {
        if (!initialIndividuals.contains(i)) {
            throw new IllegalArgumentException(ERROR_NOT_IN_LIST.format(new Object[] {i}));
        }
        if(!propertyFor(i).isEvacuated()) {
            throw new IllegalArgumentException(ERROR_NOT_EVACUATED.format(new Object[] {i}));
        }
        addToSafe(i);
        remainingIndividuals.remove(i);

        evacuatedIndividuals.add(i);
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

    public void addToDead(Individual i) {
        if (!remainingIndividuals.remove(i)) {
            throw new IllegalArgumentException(ERROR_NOT_IN_LIST.format(new Object[] {i}));
        }
        if(!propertyFor(i).isDead()) {
            throw new IllegalArgumentException(ERROR_NOT_DEAD.format(new Object[] {i}));
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
        return currentStep;
    }

    /**
     * Returns the mapping between individuals and exit cells.
     *
     * @return the mapping between individuals and exit cells
     */
    @Override
    public IndividualToExitMapping getIndividualToExitMapping() {
        return individualToExitMapping;
    }

    /**
     * Sets a mapping between individuals and exit cells.
     *
     * @param individualToExitMapping the mapping
     */
    @Override
    public void setIndividualToExitMapping(IndividualToExitMapping individualToExitMapping) {
        this.individualToExitMapping = individualToExitMapping;
    }}
