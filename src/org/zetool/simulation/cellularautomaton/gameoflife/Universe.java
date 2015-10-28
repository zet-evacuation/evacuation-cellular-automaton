package org.zetool.simulation.cellularautomaton.gameoflife;

import java.util.ArrayList;
import org.zetool.simulation.cellularautomaton.CellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Universe implements CellMatrix<SimpleCell, CellState> {

    /** Number of Cells on the x-axis. */
    private final int width;
    /** Number of Cells on the y-axis. */
    private final int height;
    /** Manages the Cells into which the room is divided. */
    private final SimpleCell[][] cells;

    protected Universe(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new SimpleCell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new SimpleCell(CellState.Dead, i, j, this);
            }
        }
    }

    /**
     * Returns the number of cells on the x-axis of the room.
     *
     * @return The number of cells on the x-axis of the room.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of cells on the y-axis of the room.
     *
     * @return The number of cells on the y-axis of the room.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Returns a list of all cells in the room.
     *
     * @return a list of all cells
     */
    @Override
    public ArrayList<SimpleCell> getAllCells() {
        ArrayList<SimpleCell> collectedCells = new ArrayList<>();
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                SimpleCell t = getCell(i, j);
            }
        }
        return collectedCells;
    }

    /**
     * Returns the cell referenced at position (x,y)
     *
     * @param x x-coordinate of the cell. 0 <= x <= width-1
     * @param y y-coordinate of the cell. 0 <= y <= height-1
     * @return The cell referenced at position (x,y). If position (x,y) is empty (in other words: does not reference any
     * cell) null is returned.
     * @throws IllegalArgumentException if the x- or the y-parameter is out of bounds.
     */
    @Override
    public SimpleCell getCell(int x, int y) throws IllegalArgumentException {
        if ((x < 0) || (x > this.width - 1)) {
            throw new IllegalArgumentException("Invalid x-value!");
        }
        if ((y < 0) || (y > this.height - 1)) {
            throw new IllegalArgumentException("Invalid y-value!");
        }
        return this.cells[x][y];
    }

    /**
     * Checks whether the cell at position (x,y) of the room exists or not
     *
     * @param x x-coordinate of the cell to be checked
     * @param y y-coordinate of the cell to be checked
     * @return "true", if the cell at position (x,y) exists, "false", if not
     */
    @Override
    public boolean existsCellAt(int x, int y) {
        if ((x < 0) || (x > (this.getWidth() - 1))) {
            return false;
        } else if ((y < 0) || (y > (this.getHeight() - 1))) {
            return false;
        } else if (this.getCell(x, y) == null) {
            return false;
        } else {
            return true;
        }
    }

    public String graphicalToString() {
        String graphic = "+---";
        for (int i = 1; i < width; i++) {
            graphic += "+---";
        }
        graphic += "+\n";

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (getCell(x, y) != null) {
                    graphic += "|";
                    graphic += getCell(x, y).graphicalToString();
                } else {
                    graphic += "| X ";
                }
            }
            graphic += "|\n";
            graphic += "+---";
            for (int i = 1; i < width; i++) {
                graphic += "+---";
            }
            graphic += "+\n";
        }

        graphic += "\n\n";

        return graphic;
    }
}
