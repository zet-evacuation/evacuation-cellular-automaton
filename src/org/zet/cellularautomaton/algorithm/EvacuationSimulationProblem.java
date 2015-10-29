package org.zet.cellularautomaton.algorithm;

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;
import org.zetool.algorithm.simulation.cellularautomaton.CellularAutomatonSimulationProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface EvacuationSimulationProblem extends CellularAutomatonSimulationProblem<EvacuationCellularAutomaton, EvacCell> {

    public ParameterSet getParameterSet();

    public CAStatisticWriter getStatisticWriter();

    public PotentialController getPotentialController();

    public EvacuationRuleSet getRuleSet();
    
}
