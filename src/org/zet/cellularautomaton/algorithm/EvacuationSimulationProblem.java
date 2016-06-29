package org.zet.cellularautomaton.algorithm;

import java.util.List;
import java.util.Map;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zetool.algorithm.simulation.cellularautomaton.CellularAutomatonSimulationProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface EvacuationSimulationProblem extends CellularAutomatonSimulationProblem<EvacuationCellularAutomatonInterface, EvacCellInterface> {

    public ParameterSet getParameterSet();

    public List<Individual> getIndividuals();

    public EvacuationRuleSet getRuleSet();
    
    /**
     * The maximal time linit in steps
     * @return the maximal time linit in seconds
     */
    public int getEvacuationStepLimit();
    
    public Map<Individual, ? extends EvacCellInterface> individualStartPositions();
    
}
