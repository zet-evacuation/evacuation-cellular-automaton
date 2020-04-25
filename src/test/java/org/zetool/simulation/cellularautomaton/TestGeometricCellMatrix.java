package org.zetool.simulation.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestGeometricCellMatrix {
    
    @Test
    public void testInstantiation() {
        GeometricCellMatrix<FakeCell> matrix = new GeometricCellMatrix<>(1,2, 4, 56);
        assertThat(matrix.getXOffset(), is(equalTo(4)));
        assertThat(matrix.getYOffset(), is(equalTo(56)));
    }
}
