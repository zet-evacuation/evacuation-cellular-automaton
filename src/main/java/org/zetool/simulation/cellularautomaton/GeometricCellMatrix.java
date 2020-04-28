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

import org.zetool.common.function.IntBiFunction;

/**
 * A geometric cell matrix is a {@link FiniteCellMatrix} that has a location. Using the index of the cells in the
 * cell matrix and the location as offset, multiple {@code GeometricCellMatrix} objects can be used to compose a larger
 * cellular automaton cell matrix.
 * 
 * @param <E> the type of cells stored in the matrix
 * @author Jan-Philipp Kappmeier
 */
public class GeometricCellMatrix<E extends Cell> extends FiniteCellMatrix<E> implements LocatedCellMatrix<E> {
    private final int xOffset;
    private final int yOffset;

    public GeometricCellMatrix(int width, int height, int xOffset, int yOffset) {
        super(width, height);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public GeometricCellMatrix(int width, int height, int xOffset, int yOffset, IntBiFunction<E> cellGenerator) {
        super(width, height, cellGenerator);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    @Override
    public int getXOffset() {
        return xOffset;
    }

    @Override
    public int getYOffset() {
        return yOffset;
    }
    
}
