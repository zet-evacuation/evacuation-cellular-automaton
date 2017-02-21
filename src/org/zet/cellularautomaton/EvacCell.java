/* zet evacuation tool copyright (c) 2007-15 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton;

import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;
import org.zetool.simulation.cellularautomaton.SquareCell;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Cellular Automaton devides a room into quadratic cells. This abstract class "EvacCell" describes such a cell,
 * which is a part of the room. It is kept abstract, because there are several special kinds of cells, such as door
 * cells, stair cells and exit cells. Generally each cell can be occupied by an individual and can be crossed with a
 * certain speed.
 *
 * @author Marcel Preuß
 * @author Jan-Philipp Kappmeier
 */
public abstract class EvacCell extends SquareCell<EvacuationCellState> implements EvacCellInterface {

    /** This character is used for graphic-like ASCII-output. */
    protected char graphicalRepresentation = ' ';
    /** Defines the Speed-Factor of the EvacCell. I.e. a value, how fast this cell can be crossed. */
    protected double speedFactor;
    /** The room to which the cell belongs. */
    protected Room room;
    /** The bounds of the cell. */
    protected Set<Direction8> bounds;
    /** Tells whether the surrounding squares are higher, equal or lower. */
    protected Map<Direction8, Level> levels;
    /** The time up to which the cell is blocked by an individuum (even if it is no longer set to the cell). */
    protected double occupiedUntil = 0;

    /**
     * Constructor defining the values of individual and speedFactor.
     *
     * @param state the state of the ca.
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than
     * or equal to 0 and smaller or equal to 1. Otherwise the standard value of the specific class which inherits from
     * EvacCell is set.
     * @param x x-coordinate of the cell in the room
     * @param y y-coordinate of the cell in the room
     */
    public EvacCell(EvacuationCellState state, double speedFactor, int x, int y) {
        this(state, speedFactor, x, y, null);
    }

    public EvacCell(EvacuationCellState state, double speedFactor, int x, int y, Room room) {
        super(state, x, y);
        this.room = room;
        setSpeedFactorSafe(speedFactor);

        // Must be in this order
        setRoom(room);

        this.bounds = EnumSet.noneOf(Direction8.class);
        this.levels = new EnumMap<>(Direction8.class);
    }

    /**
     * Swaps individuals and ignores the already occupied-check.
     *
     * @param ce
     */
    void swapIndividuals(EvacCell ce) {
        EvacCell c1 = this;
        EvacCell c2 = ce;

        Individual c1i = c1.getState().getIndividual();
        Individual c2i = c2.getState().getIndividual();
        if (c1i == null) {
            throw new java.lang.NullPointerException("Individual on cell " + c1 + " is null.");
        }
        if (c2i == null) {
            throw new java.lang.NullPointerException("Individual on cell " + c2 + " is null.");
        }
        c1.getState().setIndividual(c2i);
        c2.getState().setIndividual(c1i);
        //c1i.setCell(c2);
        //c2i.setCell(c1);
        throw new IllegalStateException("Fix individual change");
    }

    /**
     * Returns the Speed-Factor of the cell.
     *
     * @return The Speed-Factor of the cell.
     */
    @Override
    public double getSpeedFactor() {
        return speedFactor;
    }

    /**
     * Changes the Speed-Factor of the EvacCell to the specified value. This method is kept abstract because the
     * standard values may differ in the specific classes which inherit from EvacCell.
     *
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than
     * or equal to 0 and smaller or equal to 1. Otherwise the standard value of the specific class which inherits from
     * EvacCell is set.
     */
    public void setSpeedFactor(double speedFactor) {
        setSpeedFactorSafe(speedFactor);
    }
    
    private void setSpeedFactorSafe(double speedFactor) {
        if ((speedFactor >= 0) && (speedFactor <= 1)) {
            this.speedFactor = speedFactor;
        } else {
            throw new IllegalArgumentException("Speed factor " + speedFactor + " not in allowed interval [0,1]");
        }
    }

    /**
     * Returns all existing direct-neighbour-cells that are reachable of this cell
     *
     * @return ArrayList of direct-neighbour-cells of "cell"
     */
    @Override
    public List<EvacCellInterface> getNeighbours() {
        return getNeighbours(true, false);
    }

    /**
     * Returns all existing direct-neighbour-cells cell (even those that are not reachable).
     *
     * @return ArrayList of direct-neighbour-cells of "cell"
     */
    @Override
    public Collection<EvacCellInterface> getDirectNeighbors() {
        return getNeighbours(false, false);
    }

    /**
     * Returns a list of all free neighbour cells.
     *
     * @return a list of all free neighbour cells
     */
    @Override
    public List<EvacCellInterface> getFreeNeighbours() {
        return getNeighbours(true, true);
    }

    /**
     * Returns the x-coordinate of the cell.
     *
     * @return The x-coordinate of the cell.
     */
    @Override
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate of the cell.
     *
     * @return The y-coordinate of the cell.
     */
    @Override
    public int getY() {
        return this.y;
    }

    /**
     * Returns the room to which the cell belongs
     *
     * @return The room to which the cell belongs
     */
    @Override
    public Room getRoom() {
        return room;
    }

    /**
     * Manages the room to which the cell belongs. This method can only be called by classes belonging to the same
     * package in order to prevent misuse. It should only be called by Room.add(cell) in order so set the room
     * corresponding to the cell automatically.
     *
     * @param room The room to which the cell belongs
     */
    final void setRoom(Room room) {
        this.room = room;

        String s = ((room != null) ? room.getID() : "") + "-" + y + "-" + x;
        //hash = s.hashCode();
    }

    /**
     * Specifies the level difference between this cell and the cell at the relative position {@code relPosition}.
     *
     * @param relPosition The relative position of the wished neighbour cell.
     * @param level The level of the other cell according to this cell, can be higher, equal or lower.
     */
    public void setLevel(Direction8 relPosition, Level level) {
        if (this.room.existsCellAt(x + relPosition.xOffset(), y + relPosition.yOffset())) {
            this.room.getCell(x + relPosition.xOffset(),
                    y + relPosition.yOffset()).internalSetLevel(relPosition.invert(), level.getInverse());
        }

        internalSetLevel(relPosition, level);

    }

    public void internalSetLevel(Direction8 relPosition, Level level) {
        levels.put(relPosition, level);
    }

    /**
     * Specifies that this cell is separated from one of its neighbour cells by an unpenetrable bound and that thus this
     * neighbour cell cannot be directly reached from this cell.
     *
     * @param relPosition The relative position of the unreachable neighbour.
     */
    public void setUnPassable(Direction8 relPosition) {
        if (this.room.existsCellAt(x + relPosition.xOffset(), y + relPosition.yOffset())) {
            this.room.getCell(x + relPosition.xOffset(),
                    y + relPosition.yOffset()).internalSetUnPassable(relPosition.invert());
        }

        internalSetUnPassable(relPosition);
    }

    private void internalSetUnPassable(Direction8 relPosition) {
        bounds.add(relPosition);
    }

    /**
     * Specifies that the way from this cell to one of its neighbour cells is clear.
     *
     * @param relPosition The relative position of the neighbour cell.
     */
    public void setPassable(Direction8 relPosition) {
        if (this.room.existsCellAt(x + relPosition.xOffset(), y + relPosition.yOffset())) {
            this.room.getCell(x + relPosition.xOffset(),
                    y + relPosition.yOffset()).internalSetPassable(relPosition.invert());
        }
        internalSetPassable(relPosition);
    }

    private void internalSetPassable(Direction8 relPosition) {
        bounds.remove(relPosition);
    }

    /**
     * Asks whether the direct way to a neighbour cell of this cell is clear.
     *
     * @param relPosition The relative position of the neighbour cell
     * @return {@code true} if the way is clear or {@code false} if the way is blocked.
     */
    public boolean isPassable(Direction8 relPosition) {
        return !bounds.contains(relPosition);
    }

    /**
     * Returns the level difference between this cell and the cell at the relative position {@code relPosition}. If the
     * level has never been set explicitly, Equal is returned.
     *
     * @param direction The square in this direction is considered.
     * @return the level of the square in direction {@code direction} (higher, equal or lower).
     */
    @Override
    public Level getLevel(Direction8 direction) {
        return levels.containsKey(direction) ? levels.get(direction) : Level.Equal;
    }

    @Override
    /**
     * Returns a copy of itself as a new Object.
     */
    public abstract EvacCell clone();

    public EvacCell clone(boolean cloneIndividual) {
        return clone();
    }

    private int absx;
    private int absy;
    private int floor;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EvacCell other = (EvacCell) obj;
        if (this.getRoom() == null && other.getRoom() == null) {
            return getX() == other.getX() && getY() == other.getY();
        } else if (this.getRoom() != null && other.getRoom() != null) {
            if (this.getAbsoluteX() != other.getAbsoluteX()) {
                return false;
            }
            if (this.getAbsoluteY() != other.getAbsoluteY()) {
                return false;
            }
            if (this.getRoom().getFloor() != other.getRoom().getFloor()) {
                return false;
            }
            return true;            
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + getAbsoluteX();
        hash = 71 * hash + getAbsoluteY();
        hash = 71 * hash + (getRoom() == null ? 0 : getRoom().getFloor());
        return hash;
    }

    /**
     * Returns the type of the cell (D for door-, E for exit-, R for room-, S for save-, T for stair-cell), coordinates
     * of the cell, the speedfactor, if it's occupied and its room
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + "),speedfactor=" + speedFactor + ";is occupied="
                + (getState().getIndividual() == null ? "false;" : "true;") + "id=" + hashCode() + " R: " + room + ";";
    }

    /**
     * A string representation that only consists of the coordinates.
     *
     * @return a tupel of the {@code x} and {@code y} coordinates.
     */
    public String coordToString() {
        return "(" + x + "," + y + ")";
    }

    @SuppressWarnings("fallthrough")
    protected List<EvacCellInterface> getNeighbours(boolean passableOnly, boolean freeOnly) {
        List<EvacCellInterface> neighbours = new ArrayList<>();
        Room cellRoom = this.getRoom();
        for (Direction8 direction : Direction8.values()) {
            int cellx = this.getX() + direction.xOffset();
            int celly = this.getY() + direction.yOffset();
            if (cellRoom.existsCellAt(cellx, celly) && (!passableOnly || !bounds.contains(direction))
                    && (!freeOnly || cellRoom.getCell(cellx, celly).getState().getIndividual() == null)) {
                // Test again for the diagonal directions. if next to the position an individual stands. than this direction is removed!
                boolean add = true;
                switch (direction) {
                    case DownLeft:
                    case DownRight:
                    case TopLeft:
                    case TopRight:
                        boolean f1 = false;
                        boolean f2 = false;
                        if (cellRoom.existsCellAt(this.getX() + direction.xOffset(),
                                this.getY()) && cellRoom.getCell(this.getX() + direction.xOffset(),
                                        this.getY()).getState().getIndividual() != null) {
                            f1 = true;
                        } else if (cellRoom.existsCellAt(this.getX() + direction.xOffset(),
                                this.getY()) && cellRoom.getCell(this.getX() + direction.xOffset(),
                                        this.getY()).getState().getIndividual() != null) {
                            f2 = true;
                        }
                        if (f1 && f2) {
                            add = false;
                        }
                    default:
                    // nothing
                }
                add = true;
                if (add) {
                    neighbours.add(cellRoom.getCell(cellx, celly));
                } else {
                    System.err.println("Neuer Fall ist eingetreten!");
                }
            }
        }

        return neighbours;
    }

    /**
     * Returns the neighbour in a given direction.
     *
     * @param dir the direction.
     * @return the neighbour, if exists. null else.
     */
    @Override
    public EvacCellInterface getNeighbor(Direction8 dir) {
        int cellx = getX() + dir.xOffset();
        int celly = getY() + dir.yOffset();
        if (getRoom().existsCellAt(cellx, celly)) {
            return getRoom().getCell(cellx, celly);
        } else {
            return null;
        }
    }

    @Override
    public boolean isOccupied() {
        return getState().getIndividual() != null;
    }

    @Override
    public boolean isOccupied(double time) {
        return getState().getIndividual() != null || time < occupiedUntil;
    }

    @Override
    public double getOccupiedUntil() {
        return occupiedUntil;
    }

    @Override
    public void setOccupiedUntil(double occupiedUntil) {
        this.occupiedUntil = occupiedUntil;
    }

    /**
     * Returns a string with a graphic like representation of this room.
     *
     * @return a string representing this room.
     */
    public String graphicalToString() {
        return getState().getIndividual() == null ? graphicalRepresentation + " " + graphicalRepresentation
                : graphicalRepresentation + "I" + graphicalRepresentation;
    }

    protected <T extends EvacCell> T basicClone(T aClone, boolean cloneIndividual) {
        aClone.setSpeedFactor(this.getSpeedFactor());

        if (cloneIndividual && this.getState().getIndividual() != null) {
            aClone.getState().setIndividual(new Individual(this.getState().getIndividual()));
            //aClone.getState().getIndividual().setCell(aClone);
        } else {
            aClone.getState().setIndividual(this.getState().getIndividual());
        }

        aClone.room = this.room;

        for (Direction8 bound : this.bounds) {
            aClone.bounds.add(bound);
        }

        return aClone;
    }

    /**
     * Returns the direction in which the cell {@code c} lies. This has to be a neighbor cell.
     *
     * @param c a neighbor cell
     * @return the direction in which {@code c} lies
     */
    @Override
    public Direction8 getRelative(EvacCellInterface c) {
        return Direction8.getDirection(c.getAbsoluteX() - getAbsoluteX(), c.getAbsoluteY() - getAbsoluteY());
    }
}
