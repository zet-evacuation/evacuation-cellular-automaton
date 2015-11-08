package org.zetool.simulation.cellularautomaton;

import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestFiniteCellMatrix {

    private final int WIDTH = 3;
    private final int HEIGHT = 4;

    @Test
    public void testNull() {
        FiniteCellMatrix<FakeCell, Void> a = new FiniteCellMatrix<>(WIDTH, HEIGHT);
        for (int i = 0; i < WIDTH; ++i) {
            for (int j = 0; j < HEIGHT; ++j) {
                assertThat(a.getCell(i, j), is(nullValue()));
                assertThat(a.existsCellAt(i, j), is(false));
            }
        }
    }

    @Test
    public void testBoundsCheck() {
        FiniteCellMatrix<FakeCell, Void> matrix = new FiniteCellMatrix<>(WIDTH, HEIGHT, (t, u) -> getCellArray()[t][u]);
        for (int x = 0; x < WIDTH; ++x) {
            assertThat(matrix.existsCellAt(x, -1), is(false));
            assertThat(matrix.existsCellAt(x, HEIGHT), is(false));
        }
        for (int y = 0; y < HEIGHT; ++y) {
            assertThat(matrix.existsCellAt(-1, y), is(false));
            assertThat(matrix.existsCellAt(WIDTH, y), is(false));
        }
    }
    
    @Test
    public void testBoundsException() {
        FiniteCellMatrix<FakeCell, Void> a = new FiniteCellMatrix<>(WIDTH, HEIGHT);
        writeWithException(a, -1, 0);
        writeWithException(a, 0, -1);
        writeWithException(a, WIDTH, 0);
        writeWithException(a, 0, HEIGHT);
    }
    
    private void writeWithException(FiniteCellMatrix<FakeCell, Void> matrix, int x, int y) {
        try {
            matrix.setCell(x, y, null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        throw new AssertionError("No exception thrown for x=" + x + ", y=" + y);
    }
    
    @Test
    public void testReadBoundsException() {
        FiniteCellMatrix<FakeCell, Void> a = new FiniteCellMatrix<>(WIDTH, HEIGHT);
        readWithException(a, -1, 0);
        readWithException(a, 0, -1);
        readWithException(a, WIDTH, 0);
        readWithException(a, 0, HEIGHT);
    }

    private void readWithException(FiniteCellMatrix<FakeCell, Void> matrix, int x, int y) {
        try {
            matrix.getCell(x, y);
        } catch (IllegalArgumentException ex) {
            return;
        }
        throw new AssertionError("No exception thrown for x=" + x + ", y=" + y);
    }

    @Test
    public void testNotNull() {
        FakeCell[][] cells = getCellArray();
        FiniteCellMatrix<FakeCell, Void> cellMatrix = new FiniteCellMatrix<>(WIDTH, HEIGHT, (t, u) -> cells[t][u]);
        assertThat(cellMatrix.getWidth(), is(equalTo(WIDTH)));
        assertThat(cellMatrix.getHeight(), is(equalTo(HEIGHT)));
        assertCellArray(cells, cellMatrix);
    }

    @Test
    public void testPopulation() {
        FakeCell[][] cells = getCellArray();
        FiniteCellMatrix<FakeCell, Void> cellMatrix = new FiniteCellMatrix<>(WIDTH, HEIGHT);
        cellMatrix.populate((t, u) -> cells[t][u]);
        assertCellArray(cells, cellMatrix);
    }

    private FakeCell[][] getCellArray() {
        FakeCell[][] cells = new FakeCell[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; ++i) {
            for (int j = 0; j < HEIGHT; ++j) {
                cells[i][j] = new FakeCell();
            }
        }
        return cells;
    }

    private void assertCellArray(FakeCell[][] cells, FiniteCellMatrix<FakeCell, Void> a) {
        for (int i = 0; i < WIDTH; ++i) {
            for (int j = 0; j < HEIGHT; ++j) {
                assertThat(a.getCell(i, j), is(sameInstance(cells[i][j])));
            }
        }
    }

    @Test
    public void testAllIterator() {
        FiniteCellMatrix<FakeCell, Void> matrix = new FiniteCellMatrix<>(WIDTH, HEIGHT, (t, u) -> new FakeCell());
        assertSeen(matrix, WIDTH * HEIGHT);
    }
    
    @Test
    public void testAllIteratorWithNull() {
        FiniteCellMatrix<FakeCell, Void> matrix = new FiniteCellMatrix<>(WIDTH, HEIGHT, (t, u) -> new FakeCell());
        matrix.setCell(0, 0, null);
        assertSeen(matrix, WIDTH * HEIGHT - 1);
    }
    
    private void assertSeen(FiniteCellMatrix<FakeCell, Void> cellMatrix, int expected ) {
        Set<FakeCell> seen = new HashSet<>(WIDTH * HEIGHT);
        cellMatrix.getAllCells().stream().forEach(c -> seen.add(c));
        assertThat(seen.size(), is(equalTo(expected)));
    }
}
