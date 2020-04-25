package org.zet.cellularautomaton;

import java.util.Collection;
import java.util.List;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;
import org.zetool.simulation.cellularautomaton.Cell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface EvacCellInterface extends Cell<EvacuationCellState> {

    /**
     * Returns all existing direct-neighbour-cells cell (even those that are not reachable).
     *
     * @return ArrayList of direct-neighbour-cells of "cell"
     */
    Collection<EvacCellInterface> getDirectNeighbors();

    /**
     * Returns a list of all free neighbour cells.
     *
     * @return a list of all free neighbour cells
     */
    List<EvacCellInterface> getFreeNeighbours();

    /**
     * Returns the level difference between this cell and the cell at the relative position
     * {@code relPosition}. If the level has never been set explicitly, Equal is returned.
     *
     * @param direction The square in this direction is considered.
     * @return the level of the square in direction {@code direction} (higher, equal or lower).
     */
    Level getLevel(Direction8 direction);

    /**
     * Returns the neighbour in a given direction.
     *
     * @param dir the direction.
     * @return the neighbour, if exists. null else.
     */
    EvacCellInterface getNeighbor(Direction8 dir);

    /**
     * Returns all existing direct-neighbour-cells that are reachable of this cell
     *
     * @return ArrayList of direct-neighbour-cells of "cell"
     */
    List<EvacCellInterface> getNeighbours();

    double getOccupiedUntil();

    /**
     * Returns the direction in which the cell {@code c} lies. This has to be a neighbor cell.
     *
     * @param c a neighbor cell
     * @return the direction in which {@code c} lies
     */
    Direction8 getRelative(EvacCellInterface c);

    /**
     * Returns the x-coordinate of the cell.
     *
     * @return The x-coordinate of the cell.
     */
    int getX();

    /**
     * Returns the y-coordinate of the cell.
     *
     * @return The y-coordinate of the cell.
     */
    int getY();

    /**
     * Returns the room to which the cell belongs
     *
     * @return The room to which the cell belongs
     */
    Room getRoom();

    default int getAbsoluteX() {
        return getX() + (getRoom() == null ? 0 : getRoom().getXOffset());
    }

    default int getAbsoluteY() {
        return getY() + (getRoom() == null ? 0 : getRoom().getYOffset());
    }

    /**
     * Returns the Speed-Factor of the cell.
     *
     * @return The Speed-Factor of the cell.
     */
    double getSpeedFactor();

    boolean isOccupied();

    public void setOccupiedUntil(double occupiedUntil);

    public boolean isOccupied(double time);

    /**
     * Decides wether it is safe for evacuees to stand on the cell. By default cells are unsafe.
     *
     * @return {@code true} if it is safe to stand on the cell
     */
    default boolean isSafe() {
        return false;
    }

}
