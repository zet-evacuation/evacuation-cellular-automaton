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
        for( int i = 0; i < WIDTH; ++i) {
            for( int j = 0; j < HEIGHT; ++j) {
                assertThat(a.getCell(i, j), is(nullValue()));
            }
        }
    }
    
    @Test
    public void testNotNull() {
        FakeCell[][] cells = new FakeCell[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; ++i) {
            for (int j = 0; j < HEIGHT; ++j) {
                cells[i][j] = new FakeCell();
            }
        }

        FiniteCellMatrix<FakeCell, Void> a = new FiniteCellMatrix<>(WIDTH, HEIGHT, (int t, int u) -> cells[t][u]);
        assertThat(a.getWidth(), is(equalTo(WIDTH)));
        assertThat(a.getHeight(), is(equalTo(HEIGHT)));

        for (int i = 0; i < WIDTH; ++i) {
            for (int j = 0; j < HEIGHT; ++j) {
                assertThat(a.getCell(i, j), is(sameInstance(cells[i][j])));
            }
        }
    }

    @Test
    public void testAllIterator() {
        FiniteCellMatrix<FakeCell, Void> a = new FiniteCellMatrix<>(WIDTH, HEIGHT, (int t, int u) -> new FakeCell());
        Set<FakeCell> seen = new HashSet<>(WIDTH * HEIGHT);
        a.getAllCells().stream().forEach(c -> seen.add(c));
        assertThat(seen.size(), is(equalTo(WIDTH * HEIGHT)));
    }
}
