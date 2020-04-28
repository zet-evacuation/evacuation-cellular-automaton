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
package org.zet.cellularautomaton.potential;

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 * A {@code Potential} stores a distance value for each cell to an exit. The potential values are used to evaluate 
 * a route that the indiviuals take to the exit. Potential values are non-negative
 * 
 * @author Jan-Philipp Kappmeier
 */
public interface Potential extends Iterable<EvacCellInterface> {

    /**
     * Returns the potential of a specified {@link EvacCell}.
     *
     * @param cell the cell which potential should be returned
     * @return potential of the specified cell
     */
    public int getPotential(EvacCellInterface cell);

    public double getPotentialDouble(EvacCellInterface cell);

    public int getMaxPotential();

    /**
     * Checks whether a given cell has a valid potential. Especially a valid potential value is not
     * {@link #UNKNOWN_POTENTIAL_VALUE}.
     * @param cell the cell
     * @return {@code true} if the cell has a valid potential value, {@code false} otherwise
     */
    public boolean hasValidPotential(EvacCellInterface cell);
}
