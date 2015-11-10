package org.zetool.algorithm.simulation.cellularautomaton;

import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellularAutomaton;

/**
 *
 * @param <CA>
 * @param <Ce> the cell type of the cellular automaton
 * @author Jan-Philipp Kappmeier
 */
public interface CellularAutomatonSimulationProblem<CA extends CellularAutomaton<Ce>, Ce extends Cell<?>> {

    public CA getCa();

}
