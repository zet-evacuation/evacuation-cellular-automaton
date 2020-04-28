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
package org.zetool.simulation.cellularautomaton.tools;

import org.zetool.common.util.Bounds;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Orientation;

/**
 * Provides character and String definitions of the drawing style.
 * 
 * @author Jan-Philipp kappmeier
 */
public interface CellMatrixFormatterStyle {
    /**
     * Defines the char used for the boundary of the delimiter lines. The directions are for the four corners and the
     * four boundary segments joining three elements.
     * @param dir the direction
     * @return the char used to draw the bound at the given direction
     */
    char getDelimiterBound(Direction8 dir);
    
    /**
     * Returns the center element of the grid.
     * @return the center element of the grid
     */
    char getCenter();
    
    /**
     * Defines the outer bounds for the non-delimter segments
     * @param b
     * @return 
     */
    default char getBound(Bounds b) {
        return b == Bounds.LOWER || b == Bounds.UPPER ? getGrid(Orientation.HORIZONTAL) : getGrid(Orientation.VERTICAL);
    }
    
    char getGrid(Orientation c);
    
    /**
     * Returns a mark to indicate that a cell in the grid is empty.
     * @return char for empty cells
     */
    char getUndefined();
}
