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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A virtual compund {@link CellMatrix} that consists of multiple, non-overlapping cell matrices
 * creating a larger cell matrix. The composite cell matrix may contain holes.
 *
 * @param <M> the geometric matrix type with location
 * @param <E>
 * @author Jan-Philipp Kappmeier
 */
public class CompositeCellMatrix<M extends LocatedCellMatrix<E>, E extends Cell> implements CellMatrix<E> {

    int minx = Integer.MAX_VALUE;
    int miny = Integer.MAX_VALUE;
    int maxx = Integer.MIN_VALUE;
    int maxy = Integer.MIN_VALUE;

    List<M> lists = new LinkedList<>();

    public void addMatrix(M matrix) {
        lists.add(matrix);
        minx = Math.min(minx, matrix.getXOffset());
        miny = Math.min(miny, matrix.getYOffset());
        maxx = Math.max(maxx, matrix.getXOffset() + matrix.getWidth() - 1);
        maxy = Math.max(maxy, matrix.getYOffset() + matrix.getHeight() - 1);
    }

//    public void populate(IntBiFunction<E> cellGenerator) {
//        lists.stream().forEach(matrix -> matrix.populate(cellGenerator));
//    }

    @Override
    public int getWidth() {
        return lists.isEmpty() ? 0 : maxx - minx + 1;
    }

    @Override
    public int getHeight() {
        return lists.isEmpty() ? 0 : maxy - miny + 1;
    }

    @Override
    public Collection<E> getAllCells() {
        List<E> newList = new ArrayList<>();
        lists.stream().forEach(matrix -> newList.addAll(matrix.getAllCells()));
        return newList;
    }

    @Override
    public E getCell(int x, int y) {
        for (M matrix : lists) {
            if (liesInMatrix(matrix, x, y)) {
                int translatedX = x - matrix.getXOffset();
                int translatedY = y - matrix.getYOffset();
                return matrix.getCell(translatedX, translatedY);
            }
        }
        throw new IllegalArgumentException("No cell at " + x + "," + y);
    }

    /**
     * Example: xoffset = 4, yoffset = -2 widht = 3, height = 4 contained: (4,-2) ... (6, 1)
     *
     * @param matrix
     * @param x
     * @param y
     * @return
     */
    private boolean liesInMatrix(M matrix, int x, int y) {
        //4,-2 given
        int translatedX = x - matrix.getXOffset();
        int translatedY = y - matrix.getYOffset();
        if ((translatedX < 0) || (translatedX > matrix.getWidth() - 1)) {
            return false;
        }
        if ((translatedY < 0) || (translatedY > matrix.getHeight() - 1)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean existsCellAt(int x, int y) {
        return lists.stream().anyMatch(matrix -> liesInMatrix(matrix, x, y));
    }
    
    protected List<M> getMatrices() {
        return Collections.unmodifiableList(lists);
    }


}
