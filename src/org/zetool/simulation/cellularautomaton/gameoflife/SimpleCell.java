package org.zetool.simulation.cellularautomaton.gameoflife;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.zetool.common.util.Direction8;
import org.zetool.simulation.cellularautomaton.CellMatrix;
import org.zetool.simulation.cellularautomaton.SquareCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimpleCell extends SquareCell<SimpleCell, CellState> {

    public SimpleCell(CellState state, int x, int y, Universe matrix) {
        super(state, x, y, matrix);
    }

    @Override
    public Collection<SimpleCell> getDirectNeighbors() {
        ArrayList<SimpleCell> neighbours = new ArrayList<>();
        CellMatrix<SimpleCell, CellState> cellRoom = getMatrix();
        for (Direction8 direction : Direction8.values()) {
            int cellx = this.x + direction.xOffset();
            int celly = this.y + direction.yOffset();
            if (cellRoom.existsCellAt(cellx, celly) /*&& ( !bounds.contains(direction)*/) {
                if (cellRoom.existsCellAt(this.x + direction.xOffset(), this.y + direction.yOffset()) &&
                        cellRoom.getCell(this.x + direction.xOffset(), this.y + direction.yOffset()).getState() == CellState.Alive) {
                    neighbours.add(cellRoom.getCell(cellx, celly));
                }
            }
        }

        return neighbours;
    }


    @Override
    public Iterator<SimpleCell> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
