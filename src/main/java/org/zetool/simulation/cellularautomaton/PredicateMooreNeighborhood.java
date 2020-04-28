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

import java.util.function.Predicate;

/**
 * An implementation of the {@link MooreNeighborhood} that checks if neighboring cells are actually valid. Thus cells
 * having the wrong state can be excluded.
 * 
 * @param <E> the cell type
 * @param <S> the state of the cell
 * @author Jan-Philipp Kappmeier
 */
public class PredicateMooreNeighborhood<E extends SquareCell<S>, S> extends MooreNeighborhood<E> {
    /** The predicate checking if the cell's state is valid for acceptance. */
    private final Predicate<S> acceptCell;
    
    public PredicateMooreNeighborhood(CellMatrix<E> matrix) {
        super(matrix);
        acceptCell = x -> true;
    }

    public PredicateMooreNeighborhood(CellMatrix<E> matrix, Predicate<S> acceptCell) {
        super(matrix);
        this.acceptCell = acceptCell;
    }

    @Override
    protected boolean accept(E cell) {
        return acceptCell.test(cell.getState());
    }
}
