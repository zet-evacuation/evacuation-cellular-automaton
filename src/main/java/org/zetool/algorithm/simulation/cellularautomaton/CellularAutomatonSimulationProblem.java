package org.zetool.algorithm.simulation.cellularautomaton;

import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellularAutomaton;

/**
 *
 * @param <A>
 * @param <C> the cell type of the cellular automaton
 * @author Jan-Philipp Kappmeier
 */
public interface CellularAutomatonSimulationProblem<A extends CellularAutomaton<C>, C extends Cell<?>> {

    public A getCellularAutomaton();

}
