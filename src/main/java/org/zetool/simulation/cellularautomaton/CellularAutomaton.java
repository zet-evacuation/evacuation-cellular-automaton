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
package org.zetool.simulation.cellularautomaton;

/**
 * A cellular automaton is a container consisting of a cell grid and a neighborhood function. The cell grid is the
 * basic underlying datastructure of a cellular automaton. The neighborhood function returns all cells next to a given
 * cell.
 * 
 * @param <E> the cell type
 * @author Jan-Philipp Kappmeier
 */
public interface CellularAutomaton<E extends Cell<?>> {

    /**
     * Returns the dimension of the cellular automaton. Typically the dimension of a cellular automton is 1 or 2.
     *
     * @return the dimension of the cellular automaton
     */
    public int getDimension();
    
    /**
     * The neighborhood function.
     * 
     * @return the current neighborhood function.
     */
    public Neighborhood<E> getNeighborhood();
}
