package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import static org.zet.cellularautomaton.potential.TestAbstractPotential.getCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestDynamicPotential {

    @Test
    public void testSetPlotential() {
        DynamicPotential potential = new DynamicPotential() {
        };
        EvacCell c = getCell();
        potential.setPotential(c, 2.7);
        assertThat(potential.getPotential(c), is(equalTo(3)));
        assertThat(potential.getMappedCells(), contains(c));
        assertThat(potential.getMaxPotential(), is(equalTo(3)));
        assertThat(potential.getMaxPotentialDouble(), is(closeTo(2.7, 10e-6)));
    }

    @Test
    public void testDelete() {
        DynamicPotential potential = new DynamicPotential() {
        };
        EvacCell c = getCell();
        potential.setPotential(c, 3);
        potential.deleteCell(c);
        assertThat(potential.getPotential(c), is(equalTo(0)));
        assertThat(potential.getMappedCells(), is(empty()));
        assertThat(potential.getMaxPotential(), is(equalTo(0)));
    }
    

}
