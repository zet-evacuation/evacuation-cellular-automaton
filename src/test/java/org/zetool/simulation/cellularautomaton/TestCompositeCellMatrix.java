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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import org.junit.Before;
import org.junit.Test;

/**
 * The layout of the example composite cell matrix is:
 *    -1 0 1 2 3
 * -2  x x
 * -1  x x x x x
 *  0      x x x
 *  1      x x x
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestCompositeCellMatrix {

    private static final int WIDTH = 5;
    private static final int HEIGHT = 4;
    private static final int X_OFFSET = -1;
    private static final int Y_OFFSET = -2;
    private GeometricCellMatrix<FakeCell> matrix1;
    private GeometricCellMatrix<FakeCell> matrix2;
    private CompositeCellMatrix<GeometricCellMatrix<FakeCell>, FakeCell> composite;
    private final int[][] existingCellsMatrix1 = {{-1, -2}, {-1, -1}, {0, -2}, {0, -1}};
    private final FakeCell[][] cells = new FakeCell[WIDTH][HEIGHT];
    private final int[][] existingCellsMatrix2 = {
        {1, -1}, {1, 0}, {1, 1}, {2, -1}, {2, 0}, {2, 1}, {3, -1}, {3, 0}, {3, 1},};
    private final int[][] nonExisting = {
        {-1, 0}, {-1, 1}, {0, 0}, {0, 1}, {1, -2}, {2, -2}, {3, -2}};

    @Before
    public void initCellMatrix() {
        matrix1 = new GeometricCellMatrix<>(2, 2, X_OFFSET, Y_OFFSET);
        matrix2 = new GeometricCellMatrix<>(3, 3, 1, -1);
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                cells[i][j] = new FakeCell();
            }
        }
        for (int i = 2; i < 5; ++i) {
            for (int j = 1; j < 4; ++j) {
                cells[i][j] = new FakeCell();
            }
        }
        matrix1.populate((int t, int u) -> cells[t][u]);
        matrix2.populate((int t, int u) -> cells[t + 2][u + 1]);
        composite = new CompositeCellMatrix<>();
        composite.addMatrix(matrix1);
        composite.addMatrix(matrix2);
    }

    @Test
    public void offsetComputation() {
        composite = new CompositeCellMatrix<>();

        assertThat(composite.getWidth(), is(equalTo(0)));
        assertThat(composite.getHeight(), is(equalTo(0)));

        composite.addMatrix(matrix1);

        assertThat(composite.getWidth(), is(equalTo(2)));
        assertThat(composite.getHeight(), is(equalTo(2)));

        composite.addMatrix(matrix2);

        assertThat(composite.getWidth(), is(equalTo(WIDTH)));
        assertThat(composite.getHeight(), is(equalTo(HEIGHT)));
    }

    @Test
    public void containsFails() {
        assertExists(composite, existingCellsMatrix1, true);
        assertExists(composite, existingCellsMatrix2, true);
        assertExists(composite, nonExisting, false);
    }

    private static void assertExists(CompositeCellMatrix<?, ?> composite, int[][] cells,
            boolean expected) {
        for (int i = 0; i < cells.length; ++i) {
            assertAssignment(composite::getCell, cells[i][0], cells[i][1], expected,
                    IllegalArgumentException.class);
        }
    }

    public static <T, E extends Exception> void assertAssignment(BiConsumer<T, T> consumer,
            T value1, T value2, boolean shouldAccept, Class<E> exceptionType) {
        try {
            consumer.accept(value1, value2);
        } catch (Exception ex) {
            if (shouldAccept) {
                throw new AssertionError("Should not fail for " + value1 + "," + value2);
            } else {
                assertThat(ex, is(instanceOf(exceptionType)));
                return;
            }
        }
        if (!shouldAccept) {
            throw new AssertionError("Should fail for " + value1 + "," + value2);
        }
    }

    @Test
    public void testIterator() {
        Set<Integer> matrix1Cells = new HashSet<>();
        Set<Integer> matrix2Cells = new HashSet<>();
        Set<Integer> allCells = new HashSet<>();

        int i = 0;
        for (FakeCell cell : composite.getAllCells()) {
            if (cellInList(existingCellsMatrix1, cell)) {
                matrix1Cells.add(i);
            }
            if (cellInList(existingCellsMatrix2, cell)) {
                matrix2Cells.add(i);
            }
            allCells.add(i++);
        }
        assertThat(matrix1Cells.size(), is(equalTo(4)));
        assertThat(matrix2Cells.size(), is(equalTo(9)));
        assertThat(allCells.size(), is(equalTo(13)));
    }

    /**
     * Checks wether a cell is contained in a list of positions. The positions are specified as
     * integer tuples of {@literal x},{@literal y} tuples.
     *
     * @param positions an array of integer tuples (as 2-dimensional arrays)
     * @param cell the cell to be checked
     * @return {@code true} if the cell is at one of the indices
     */
    private boolean cellInList(int[][] positions, FakeCell cell) {
        for (int[] xy : positions) {
            if (composite.getCell(xy[0], xy[1]) == cell) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void getCell() {
        int counter = 0;
        for (int i = 0; i < WIDTH; ++i) {
            for (int j = 0; j < HEIGHT; ++j) {
                int newX = i + X_OFFSET;
                int newY = j + Y_OFFSET;
                if( cells[i][j] != null ) {
                    counter++;
                    assertThat(composite.existsCellAt(newX, newY), is(true));
                    assertThat(cells[i][j],
                            is(sameInstance(composite.getCell(newX, newY))));
                } else {
                    assertThat(composite.existsCellAt(newX, newY), is(false));
                    try {
                        composite.getCell(newX, newY);
                    } catch (IllegalArgumentException e) {
                        // expected
                        continue;
                    }
                    throw new AssertionError("Cell at " + newX + "," + newY);
                }
            }
        }
        assertThat(counter, is(equalTo(13)));
    }
}
