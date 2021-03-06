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

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportCell extends BaseTeleportCell<TeleportCell> {

    boolean teleportFailed = false;

    /**
     * Constructs an empty DoorCell which is NOT connected with any other DoorCell and has the standard Speed-Factor
     * "STANDARD_DOORCELL_SPEEDFACTOR".
     *
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public TeleportCell(int x, int y) {
        this(new EvacuationCellState(null), DoorCell.STANDARD_DOORCELL_SPEEDFACTOR, x, y);
    }

    /**
     * Constructs an empty DoorCell which is NOT connected with any other DoorCell and has a speed-factor of
     * {@code speedfactor}
     *
     * @param speedFactor The speedfactor for this cell
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public TeleportCell(double speedFactor, int x, int y) {
        this(speedFactor, x, y, null);
    }

    public TeleportCell(double speedFactor, int x, int y, Room room) {
        super(speedFactor, x, y, room);
        graphicalRepresentation = '?';
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
    public TeleportCell(EvacuationCellState individual, double speedFactor, int x, int y) {
        super(individual, speedFactor, x, y);
        graphicalRepresentation = '?';
    }

    /**
     * Adds a TeleportCell which is connected to this TeleportCell.
     *
     * @param teleportTarget Defines the reference to the Door-Cell of the room you can enter by using this Door-Cell of
     * the current room.
     * @throws IllegalArgumentException if the parameter "Door" is null or if "Door" has already been added to the list
     * of doors.
     */
    @Override
    public void addTarget(TeleportCell teleportTarget) {
        if (targetCount() == 0) {
            addTargetSimple(teleportTarget);
        } else {
            throw new IllegalStateException("Already a target set: " + teleportTarget);
        }
    }

    @Override
    public void removeTarget(TeleportCell door) {
        teleportTargets.clear();
    }

    public boolean isTeleportFailed() {
        return teleportFailed;
    }

    public void setTeleportFailed(boolean teleportFailed) {
        this.teleportFailed = teleportFailed;
    }

    /**
     * Returns a copy of itself as a new Object.
     * @return 
     */
    @Override
    public TeleportCell clone() {
        return clone(false);
    }

    @Override
    public TeleportCell clone(boolean cloneIndividual) {
        TeleportCell aClone = new TeleportCell(this.getX(), this.getY());
        basicClone(aClone, cloneIndividual);
        for (TeleportCell cell : teleportTargets) {
            aClone.addTarget(cell);
        }
        return aClone;
    }

    @Override
    public String toString() {
        return "T;" + super.toString();
    }

    private int usedInTimeStep = -1;

    public void setUsedInTimeStep(int i) {
        this.usedInTimeStep = i;
    }

    public int getUsedInTimeStep() {
        return usedInTimeStep;
    }

}
