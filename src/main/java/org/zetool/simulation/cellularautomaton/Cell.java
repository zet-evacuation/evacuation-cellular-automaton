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
package org.zetool.simulation.cellularautomaton;

/**
 * @param <S> the state the cell is into
 * @author Jan-Philipp Kappmeier
 */
public interface Cell<S> {

    /**
     * Returns the state of the cell. The state is one of finitly many possible states.
     * 
     * @return the state of the cell
     */
    public S getState();
    
    /**
     * Returns the number of sides of the cell. Typically, a two dimensional cell is a triangle, a square or a hexagon.
     *
     * @return the number of sides of the cell
     */
    public int getSides();
}
