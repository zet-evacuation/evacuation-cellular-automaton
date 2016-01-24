package org.zet.cellularautomaton.algorithm.state;

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
public interface EvacuationState extends PropertyAccess, Iterable<Individual>{
    @Override
    public IndividualProperty propertyFor(Individual i);
    /**
     * Returns the current time step of the evacuation simulation.
     * 
     * @return the current time step of the evacuation simulation
     */
    public int getTimeStep();

    /**
     * Gets the minimal number of time steps that are necessary to finish all (virtually not discrete) movements.
     *
     * @return the time necessary to complete the simulation at least
     */
    public int getNecessaryTime();

    // only necessary in concrete implementation?
    public void markIndividualForRemoval(Individual individual);
    
    public void removeMarkedIndividuals();    
    
    // to be moved
    public void increaseDynamicPotential(EvacCell targetCell);

    // To be removed from the interface completely:
    public CAStatisticWriter getStatisticWriter();
    public EvacuationCellularAutomatonInterface getCellularAutomaton();
    public ParameterSet getParameterSet();

}
