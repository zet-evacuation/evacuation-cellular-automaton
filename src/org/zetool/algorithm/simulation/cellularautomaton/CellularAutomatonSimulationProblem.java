package org.zetool.algorithm.simulation.cellularautomaton;

import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellularAutomaton;

/**
 *
 * @param <Ce> the cell type of the cellular automaton
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonSimulationProblem<Ce extends Cell<Ce, ?>> {

    CellularAutomaton<Ce, ?> ca;

    public CellularAutomatonSimulationProblem(CellularAutomaton<Ce, ?> ca) {
        this.ca = ca;
    }

    public CellularAutomaton<Ce, ?> getCa() {
        return ca;
    }

}
