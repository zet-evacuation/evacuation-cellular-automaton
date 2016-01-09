package org.zet.cellularautomaton.algorithm;

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 * Provides information about the current evacuation simulation to rules.
 * 
 * @author Jan-Philipp Kappmeier
 */
public interface EvacuationState {
    /**
     * Returns the current time step of the evacuation simulation.
     * 
     * @return the current time step of the evacuation simulation
     */
    public int getTimeStep();

    /**
     * Sets a new minimal number of time steps that are necessary to finish all (virtually non discrete) movements.
     *
     * @param neededTime the time step
     */
    public void setNeededTime(int neededTime);

    /**
     * Gets the minimal number of time steps that are necessary to finish all (virtually not discrete) movements.
     *
     * @return the time
     */
    public int getNeededTime();

    public CAStatisticWriter getStatisticWriter();

    public void swapIndividuals(EvacCell cell1, EvacCell cell2);

    public void moveIndividual(EvacCell from, EvacCell targetCell);

    public void increaseDynamicPotential(EvacCell targetCell);

    public EvacuationCellularAutomatonInterface getCellularAutomaton();

    public void markIndividualForRemoval(Individual individual);

    public ParameterSet getParameterSet();
    
    public IndividualState getIndividualState();
}
