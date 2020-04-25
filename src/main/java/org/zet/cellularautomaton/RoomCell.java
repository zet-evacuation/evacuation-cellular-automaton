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

/**
 * A Room-EvacCell is the standard cell-type which is used to build rooms. It inherits its properties and methods from
 * the abstract class cell.
 *
 * @author Marcel Preuß
 *
 */
public class RoomCell extends EvacCell implements Cloneable {

    /** Constant defining the standard Speed-Factor of a Room-EvacCell. */
    public static final double STANDARD_ROOMCELL_SPEEDFACTOR = 1d;

    /**
     * Constructor defining an empty (not occupied) Room-EvacCell with the standard Speed-Factor
     * "STANDARD_ROOMCELL_SPEEDFACTOR".
     *
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public RoomCell(int x, int y) {
        this(new EvacuationCellState(null), RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR, x, y);
    }

    /**
     * Constructor defining an empty Room-EvacCell with a manual-set Speed-Factor.
     *
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than
     * or equal to 0 and smaller or equal to 1. Otherwise the standard value "STANDARD_ROOMCELL_SPEEDFACTOR" is set.
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public RoomCell(double speedFactor, int x, int y) {
        this(new EvacuationCellState(null), speedFactor, x, y);
    }

    public RoomCell(double speedFactor, int x, int y, Room room) {
        this(new EvacuationCellState(null), speedFactor, x, y, room);
    }

    /**
     * Constructor defining the value of individual. The value of SpeedFactor will be the standard value
     * "STANDARD_ROOMCELL_SPEEDFACTOR".
     *
     * @param state Defines the individual that occupies the cell. If the cell is not occupied, the value is set to
     * {@code null}.
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @p
     * aram y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public RoomCell(EvacuationCellState state, int x, int y) {
        this(state, RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR, x, y);
    }

    /**
     * Constructor defining the values of individual and speedFactor.
     *
     * @param state Defines the individual that occupies the cell. If the cell is not occupied, the value is set to
     * {@code null}.
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than
     * or equal to 0 and smaller or equal to 1. Otherwise the standard value "STANDARD_ROOMCELL_SPEEDFACTOR" is set.
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @p
     * aram y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public RoomCell(EvacuationCellState state, double speedFactor, int x, int y) {
        this(state, speedFactor, x, y, null);
    }

    public RoomCell(EvacuationCellState state, double speedFactor, int x, int y, Room room) {
        super(state, speedFactor, x, y, room);
        if (speedFactor != STANDARD_ROOMCELL_SPEEDFACTOR) {
            graphicalRepresentation = 'D';
        }
    }

    /**
     * Returns a copy of itself as a new Object.
     * @return 
     */
    @Override
    public RoomCell clone() {
        return clone(false);
    }

    @Override
    public RoomCell clone(boolean cloneIndividual) {
        RoomCell aClone = new RoomCell(this.getX(), this.getY());
        basicClone(aClone, cloneIndividual);
        return aClone;
    }

    @Override
    public String toString() {
        return "R;" + super.toString();
    }
}
