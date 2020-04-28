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

import java.util.Collection;

/**
 * @param <E> the cell type
 * @author Jan-Philipp Kappmeier
 */
public interface CellMatrix<E extends Cell> {

    public int getWidth();

    public int getHeight();

    public Collection<E> getAllCells();

    public E getCell(int x, int y);

    /**
     * Checks whether the cell at position (x,y) of the room exists or not
     *
     * @param x x-coordinate of the cell to be checked
     * @param y y-coordinate of the cell to be checked
     * @return "true", if the cell at position (x,y) exists, "false", if not
     */
    public boolean existsCellAt(int x, int y);
}
