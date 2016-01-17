package org.zet.cellularautomaton.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private final IndividualState individualState = new IndividualState();
    public CAStatisticWriter caStatisticWriter;
    private final ParameterSet parameterSet;
    private final EvacuationCellularAutomatonInterface ca;
    private final Map<Individual, IndividualProperty> individualProperties;
    
    
    /**
     * The minimal number of steps that is needed until all movements are FINISHED.
     */
    private int neededTime;
    int step = 0;
    /**
     * An {@code ArrayList} marked to be removed.
     */
    private final List<Individual> markedForRemoval = new ArrayList<>();

    public MutableEvacuationState(ParameterSet parameterSet, EvacuationCellularAutomatonInterface ca,
            List<Individual> individuals) {
        this.parameterSet = parameterSet;
        this.ca = ca;
        individualProperties = new HashMap<>();
        for( Individual i : individuals) {
            individualProperties.put(i, new IndividualProperty(i));
            individualState.addIndividual(i);
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
        if (!individualState.getRemainingIndividuals().contains(i)) {
            throw new IllegalArgumentException("Individual " + i + " not in list!");
        }
        markedForRemoval.add(i);
    }

    @Override
    public void removeMarkedIndividuals() {
        markedForRemoval.stream().forEach(individual -> {
            individualState.setIndividualEvacuated(individual);
            getCellularAutomaton().setIndividualEvacuated(individual);
        });
        markedForRemoval.clear();
    }

    @Override
    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    @Override
    public IndividualState getIndividualState() {
        return individualState;
    }


    /**
     * Calculates the number of individuals that died by a specified death cause.
     *
     * @param deathCause the death cause
     * @return the number of individuals died by the death cause
     */
    public int getDeadIndividualCount(DeathCause deathCause) {
        int count = 0;
        count = individualState.getDeadIndividuals().stream().filter(i -> propertyFor(i).getDeathCause() == deathCause).
                map(_item -> 1).reduce(count, Integer::sum);
        return count;
    }

}
