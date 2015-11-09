package org.zetool.simulation.cellularautomaton.gameoflife;

import org.zetool.simulation.cellularautomaton.CellMatrix;
import org.zetool.simulation.cellularautomaton.SquareCellularAutomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GameOfLife extends SquareCellularAutomaton<SimpleCell, CellState> {

    public GameOfLife(CellMatrix<SimpleCell, CellState> matrix) {
        super(matrix);
    }
    
}
