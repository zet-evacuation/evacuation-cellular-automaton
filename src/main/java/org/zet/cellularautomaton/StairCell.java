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

/**
 * A Stair-EvacCell is special type of cell and therefore inherits properties and methods from the abstract class
 * EvacCell. Stair-Cells usually have a lower Speed-Factor than other cells and therefore individuals usually move
 * slower across this type of cell.
 *
 * @author Marcel Preuß
 *
 */
public class StairCell extends EvacCell implements Cloneable, Stairs {

    /** Constant defining the standard Speed-Factor of a Stair-EvacCell, which is usually &lt; 1. */
    public static final double STANDARD_STAIRCELL_UP_SPEEDFACTOR = 0.5d;
    public static final double STANDARD_STAIRCELL_DOWN_SPEEDFACTOR = 0.6d;

    private double speedFactorUp;
    private double speedFactorDown;

    /**
     * This constructor creates an empty Stair-EvacCell with the standard Speed-Factor used for this special cell-type.
     *
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public StairCell(int x, int y) {
        this(new EvacuationCellState(null), RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR, x, y);
        this.setDownSpeedFactor(STANDARD_STAIRCELL_DOWN_SPEEDFACTOR);
        this.setUpSpeedFactor(STANDARD_STAIRCELL_UP_SPEEDFACTOR);
    }

    public StairCell(double speedFactor, double speedFactorUp, double speedFactorDown, int x, int y) {
        this(new EvacuationCellState(null), speedFactor, x, y);
        graphicalRepresentation = '/';
        this.setDownSpeedFactor(speedFactorDown);
        this.setUpSpeedFactor(speedFactorUp);
    }

    /**
     * Constructor defining the values of individual and speedFactor.
     *
     * @param state Defines the individual that occupies the cell. If the cell is not occupied, the value is set to
     * {@code null}.
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than 0
     * and smaller or equal to 1. Otherwise the standard value "STANDARD_STAIRCELL_SPEEDFACTOR" is set.
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public StairCell(EvacuationCellState state, double speedFactor, int x, int y) {
        super(state, speedFactor, x, y, null);
        this.setDownSpeedFactor(STANDARD_STAIRCELL_DOWN_SPEEDFACTOR);
        this.setUpSpeedFactor(STANDARD_STAIRCELL_UP_SPEEDFACTOR);
    }

    /**
     * Changes the Speed-Factor of the Stair-EvacCell to the specified value.
     *
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than
     * or equal to 0 and smaller or equal to 1. Otherwise the standard value "STANDARD_STAIRCELL_UP_SPEEDFACTOR" is set.
     */
    public final void setUpSpeedFactor(double speedFactor) {
        if ((speedFactor > 0) && (speedFactor <= 1)) {
            this.speedFactorUp = speedFactor;
        } else {
            throw new IllegalArgumentException("Up speed factor not in ]0, 1]: " + speedFactor);
        }
    }

    /**
     * Changes the Speed-Factor of the Stair-EvacCell to the specified value.
     *
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than
     * or equal to 0 and smaller or equal to 1. Otherwise the standard value "STANDARD_STAIRCELL_DOWN_SPEEDFACTOR" is
     * set.
     */
    public final void setDownSpeedFactor(double speedFactor) {
        if ((speedFactor > 0) && (speedFactor <= 1)) {
            this.speedFactorDown = speedFactor;
        } else {
            throw new IllegalArgumentException("Down speed factor not in ]0, 1]: " + speedFactor);
        }
    }

    public double getSpeedFactorDown() {
        return speedFactorDown;
    }

    public double getSpeedFactorUp() {
        return speedFactorUp;
    }

    /**
     * Returns a copy of itself as a new Object.
     * @return 
     */
    @Override
    public StairCell clone() {
        return clone(false);
    }

    @Override
    public StairCell clone(boolean cloneIndividual) {
        StairCell aClone = new StairCell(this.getX(), this.getY());
        basicClone(aClone, cloneIndividual);
        return aClone;
    }

    @Override
    public String toString() {
        return "T;" + super.toString();
    }

    /**
     * Calculate a factor that is later multiplied with the speed, this factor is only != 1 for stair cells to give
     * different velocities for going a stair up or down.
     *
     * @param direction
     * @return
     */
    @Override
    public double getStairSpeedFactor(Direction8 direction) {
        double stairSpeedFactor = 1;
        Level lvl = getLevel(direction);
        if (lvl == Level.Higher) {
            stairSpeedFactor = getSpeedFactorUp();
        } else if (lvl == Level.Lower) {
            stairSpeedFactor = getSpeedFactorDown();
        }
        return stairSpeedFactor;
    }

}
