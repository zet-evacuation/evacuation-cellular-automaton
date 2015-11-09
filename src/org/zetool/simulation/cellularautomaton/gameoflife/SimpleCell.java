package org.zetool.simulation.cellularautomaton.gameoflife;

import org.zetool.simulation.cellularautomaton.SquareCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimpleCell extends SquareCell<CellState> {

    public SimpleCell(CellState state, int x, int y) {
        super(state, x, y);
    }

    /**
     * Returns a string with a graphic like representation of this room.
     *
     * @return a string representing this room.
     */
    public String graphicalToString() {
        return getState() == CellState.Alive ? " ■ " : "   ";
    }
}
