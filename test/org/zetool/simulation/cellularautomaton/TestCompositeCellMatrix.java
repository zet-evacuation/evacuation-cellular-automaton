package org.zetool.simulation.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestCompositeCellMatrix {

    @Test
    public void test() {
        GeometricCellMatrix<FakeCell> matrix1 = new GeometricCellMatrix<>(2, 2, -1, -2);
        GeometricCellMatrix<FakeCell> matrix2 = new GeometricCellMatrix<>(3, 3, 1, -1);

        CompositeCellMatrix<FakeCell> composite = new CompositeCellMatrix<>();
        assertThat(composite.getWidth(), is(equalTo(0)));
        assertThat(composite.getHeight(), is(equalTo(0)));

        composite.addMatrix(matrix1);

        assertThat(composite.getWidth(), is(equalTo(2)));
        assertThat(composite.getHeight(), is(equalTo(2)));

        composite.addMatrix(matrix2);

        assertThat(composite.getWidth(), is(equalTo(5)));
        assertThat(composite.getHeight(), is(equalTo(4)));
    }
}
