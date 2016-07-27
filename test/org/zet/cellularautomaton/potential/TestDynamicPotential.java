package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.potential.TestAbstractPotential.getCell;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestDynamicPotential {
    private final Mockery context = new Mockery();

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
    
    @Test
    public void increaseDynamicPotential() {
        DynamicPotential potential = new DynamicPotential();
        EvacCellInterface cell = context.mock(EvacCellInterface.class);

        potential.increase(cell);
        
        assertThat(potential.getPotential(cell), is(equalTo(1)));
        
        potential.increase(cell);
        
        assertThat(potential.getPotential(cell), is(equalTo(2)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void decreaseFailsForNonExisting() {
        DynamicPotential potential = new DynamicPotential();
        
        EvacCellInterface cell = context.mock(EvacCellInterface.class);
        potential.decrease(cell);
    }
    
    @Test
    public void decreasePotential() {
        DynamicPotential potential = new DynamicPotential();
        
        EvacCellInterface cell = context.mock(EvacCellInterface.class);
        potential.setPotential(cell, 3);
        potential.decrease(cell);
        
        assertThat(potential.getPotential(cell), is(equalTo(2)));
    }
    
    @Test
    public void decreasePotentialVanishes() {
        DynamicPotential potential = new DynamicPotential();
        
        EvacCellInterface cell = context.mock(EvacCellInterface.class);
        potential.setPotential(cell, 1);
        potential.decrease(cell);
        
        assertThat(potential.getPotential(cell), is(equalTo(0)));
        assertThat(potential.hasValidPotential(cell), is(false));
    }
}
