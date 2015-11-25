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

import org.zet.cellularautomaton.potential.StaticPotential;

/**
 * A Save-Cell is special type of cell and therefore inherits properties and methods from the abstract class Cell. When
 * an individual enters this special cell, it is evacuated. But in contrast to a Room-Cell, individuals do not leave
 * this cell immediately. They wait until they become evacuated.
 *
 * @author Marcel Preuß
 *
 */
public class SaveCell extends TargetCell implements Cloneable {

    /** Constant defining the standard Speed-Factor of a Save-Cell, which may be &lt; 1. */
    public static final double STANDARD_SAVECELL_SPEEDFACTOR = 0.8d;

    /**
     * This constructor creates an empty Save-Cell with the standard SaveCell-Speed-Factor
     * "STANDARD_SAVECELL_SPEEDFACTOR".
     *
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public SaveCell(int x, int y) {
        this(SaveCell.STANDARD_SAVECELL_SPEEDFACTOR, x, y);
    }

    /**
     * This constructor creates an empty Save-Cell with a manual set Speed-Factor.
     *
     * @param speedFactor Defines how fast the cell can be crossed. The value should be a rational number greater than
     * or equal to 0 and smaller or equal to 1. Otherwise the standard value "STANDARD_SAVECELL_SPEEDFACTOR" is set.
     * @param x x-coordinate of the cell in the room, 0 &lt;= x &lt;= width-1
     * @param y y-coordinate of the cell in the room, 0 &lt;= y &lt;= height-1
     */
    public SaveCell(double speedFactor, int x, int y) {
        this(speedFactor, x, y, null);
    }

    public SaveCell(double speedFactor, int x, int y, Room room) {
        super(new EvacuationCellState(null), speedFactor, x, y, room);
        graphicalRepresentation = '*';
        exitPotential = null;
    }

    /**
     * Returns a copy of itself as a new Object.
     */
    @Override
    public SaveCell clone() {
        return clone(false);
    }

    @Override
    public SaveCell clone(boolean cloneIndividual) {
        SaveCell aClone = new SaveCell(this.getX(), this.getY());
        basicClone(aClone, cloneIndividual);
        return aClone;
    }

    @Override
    public String toString() {
        return "S;" + super.toString();
    }

    private StaticPotential exitPotential;

    public StaticPotential getExitPotential() {
        return exitPotential;
    }

    public void setExitPotential(StaticPotential sp) {
        exitPotential = sp;
    }

    @Override
    public String getName() {
        return "Save Area";
    }

    @Override
    public boolean isSafe() {
        return true;
    }
    
    
}
