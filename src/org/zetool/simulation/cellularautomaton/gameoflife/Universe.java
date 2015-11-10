package org.zetool.simulation.cellularautomaton.gameoflife;

import org.zetool.simulation.cellularautomaton.FiniteCellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Universe extends FiniteCellMatrix<SimpleCell> {

    protected Universe(int width, int height) {
        super(width, height);
        this.populate((int t, int u) -> new SimpleCell(BinaryCellState.DEAD, t, u));
    }
}
