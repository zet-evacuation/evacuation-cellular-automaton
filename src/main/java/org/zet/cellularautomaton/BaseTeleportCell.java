/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <T>
 * @author Jan-Philipp Kappmeier
 */
public abstract class BaseTeleportCell<T> extends EvacCell implements Cloneable {

    /** Keeps a list of all possible targets which are of a specified type {@code T}. */
    protected List<T> teleportTargets;

    public BaseTeleportCell(double speedFactor, int x, int y, Room room) {
        super(new EvacuationCellState(null), speedFactor, x, y, room);
        graphicalRepresentation = '!';
        teleportTargets = new ArrayList<>();
    }

    /**
     * Constructs a DoorCell with the defined values.
     *
     * @param individual Defines the individual that occupies the cell. If the cell is not occupied, the value is set to
     * "null".
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than
     * or equal to 0 and smaller or equal to 1. Otherwise the standard value "STANDARD_DOORCELL_SPEEDFACTOR" is set.
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public BaseTeleportCell(EvacuationCellState individual, double speedFactor, int x, int y) {
        super(individual, speedFactor, x, y);
        teleportTargets = new ArrayList<>();
        graphicalRepresentation = '!';
    }

    /**
     * Returns a cell you can reach by using this {@code BaseTeleportCell}.
     *
     * @return The door defined by the parameter index.
     * @param index The index which defines the requested DoorCell.
     */
    public T getTarget(int index) {
        return teleportTargets.get(index);
    }

    /**
     * Adds a DoorCell which is connected to this DoorCell and registers itself as a connected DoorCell in "door".
     *
     * @param target defines the reference to the Door-EvacCell of the room you can enter by using this Door-EvacCell of
     * the current room.
     * @throws IllegalArgumentException if the parameter {@code target} is {@literal null} or if the {@code target}
     * has already been added to the list of targets.
     */
    public abstract void addTarget(T target);

    /**
     * Removes the specified door.
     *
     * @param target the target which shall be removed.
     */
    public abstract void removeTarget(T target);

    /**
     * Calls remove targets on all of the targets.
     */
    public void removeAllTargets() {
        while (!teleportTargets.isEmpty()) {
            this.removeTarget(teleportTargets.get(0));
        }
    }

    /**
     * Returns the number of target cells which are connected to this cell. Normal neighbour cells are not counted.
     *
     * @return the number of targets which are connected to this cell.
     */
    public int targetCount() {
        return teleportTargets.size();
    }

    /**
     * Checks whether the list of nextDoorCells contains the specified door.
     *
     * @param target The door to checked (if in list or not).
     * @return "True", if the list of teleportTargets contains "door", false if not.
     */
    public boolean containsTarget(T target) {
        return teleportTargets.contains(target);
    }

    /**
     * Returns the index of the defined DoorCell "door".
     *
     * @param target The door whose index is requested.
     * @return The index of the defined DoorCell "door", or -1, if the specified door is not contained in the list of
     * DoorCells.
     */
    public int getIndexOf(T target) {
        return teleportTargets.indexOf(target);
    }

    /**
     * Adds a cell to the list of targets but checks if it was not contained in the list before. No additional check is
     * performed.
     *
     * @param target the target cell
     */
    protected void addTargetSimple(T target) {
        if (!teleportTargets.contains(target)) {
            teleportTargets.add(target);
        }
    }
}
