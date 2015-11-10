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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.zetool.common.util.Direction8;

/**
 * @param <E> the cell type
 * @author Jan-Philipp Kappmeier
 */
public class MooreNeighborhood<E extends SquareCell<?>> implements Neighborhood<E> {

    private final CellMatrix<E> matrix;

    public MooreNeighborhood(CellMatrix<E> matrix) {
        this.matrix = matrix;
    }
    
    @Override
    public Collection<E> getNeighbors(E cell) {
        List<E> neighbours = new ArrayList<>();
        for (Direction8 direction : Direction8.values()) {
            int cellx = cell.x + direction.xOffset();
            int celly = cell.y + direction.yOffset();
            if (matrix.existsCellAt(cellx, celly) && accept(matrix.getCell(cellx, celly))) {
                neighbours.add(matrix.getCell(cellx, celly));
            }
        }

        return neighbours;
    }
    
    protected boolean accept(E cell) {
        return true;
    }
}
