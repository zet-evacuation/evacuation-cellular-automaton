package org.zet.cellularautomaton.potential;

import java.util.Iterator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellState;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractPotential {
    private static class NoIteratorAbstractPotential extends AbstractPotential {
            @Override
            public Iterator<EvacCellInterface> iterator() {
                throw new UnsupportedOperationException("Unsupported.");
            }
        };

    static EvacCell getCell(int x) {
        return new EvacCell(new EvacuationCellState(null), 1, x, 0) {

            @Override
            public EvacCell clone() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };        
    }
    
    @Test
    public void initializedPotential() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        assertThat(AbstractPotential.INVALID, is(equalTo(-1)));
        assertThat(potential.getMaxPotential(), is(equalTo(AbstractPotential.INVALID)));
        assertThat(potential.getMappedCells(), is(empty()));
        assertThat(potential.hasValidPotential(getCell(0)), is(false));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void initializedPotentialGetNonexistent() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        potential.getPotential(getCell(0));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void initializedPotentialGetNonexistentDouble() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        potential.getPotentialDouble(getCell(0));
    }
    
    @Test
    public void testContained() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        EvacCell c = getCell(0);
        potential.setPotential(c, 3);
        assertThat(potential.hasValidPotential(c), is(true));
    }
    
    @Test
    public void setPotentialValue() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        EvacCell c = getCell(0);
        potential.setPotential(c, 4.3);
        assertThat(potential.getPotential(c), is(equalTo(4)));
        assertThat(potential.getPotentialDouble(c), is(closeTo(4.3, 10e-8)));
        assertThat(potential.getMappedCells(), contains(c));
        assertThat(potential.getMappedCells(), hasSize(1));
        
        potential.setPotential(c, 4.9);
        assertThat(potential.getPotential(c), is(equalTo(5)));
        assertThat(potential.getPotentialDouble(c), is(closeTo(4.9, 10e-8)));
    }
    
    @Test(expected = NullPointerException.class)
    public void setPotentialNullFails() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        potential.setPotential(null, 3);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteEmptyFails() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        potential.deleteCell(getCell(0));
    }
    
    @Test
    public void testDelete() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        EvacCell c = getCell(0);
        potential.setPotential(c, 3);
        potential.deleteCell(c);
        assertThat(potential.getMappedCells(), is(empty()));
        assertThat(potential.getMaxPotential(), is(equalTo(AbstractPotential.INVALID)));
    }
    
    @Test
    public void updateMaxPotentialWhenAdding() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        EvacCell c1 = getCell(0);
        potential.setPotential(c1, 3);
        assertThat(potential.getMaxPotential(), is(equalTo(3)));
        EvacCell c2 = getCell(1);
        potential.setPotential(c2, 4);
        assertThat(potential.getMaxPotential(), is(equalTo(4)));
        EvacCell c3 = getCell(2);
        potential.setPotential(c3, 2);
        assertThat(potential.getMaxPotential(), is(equalTo(4)));
        
        // Now set the cell with maximum value to a lower one such that the maximum potential decreases
        potential.setPotential(c2, 3);
        assertThat(potential.getMaxPotential(), is(equalTo(3)));
        
        // Set the potential of an existing cell to a lower value. should not change the maximum.
        potential.setPotential(c1, 1);
        assertThat(potential.getMaxPotential(), is(equalTo(3)));
        
        // Set the potential of an existing cell to a higher value
        potential.setPotential(c2, 5);
        assertThat(potential.getMaxPotential(), is(equalTo(5)));
    }
    
    @Test
    public void updateMaxPotentialWhenDeleting() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        EvacCell c1 = getCell(0);
        potential.setPotential(c1, 3);
        EvacCell c2 = getCell(1);
        potential.setPotential(c2, 4);
        EvacCell c3 = getCell(2);
        potential.setPotential(c3, 2);
        
        potential.deleteCell(c1);
        assertThat(potential.getMaxPotential(), is(equalTo(4)));
        potential.deleteCell(c2);
        assertThat(potential.getMaxPotential(), is(equalTo(2)));
        potential.deleteCell(c3);
    }
    
    @Test
    public void updateMaxPotentialWhenResetting() {
        AbstractPotential potential = new NoIteratorAbstractPotential();
        EvacCell c = getCell(0);
        potential.setPotential(c, 3);
        potential.setPotential(c, 2);
        assertThat(potential.getMaxPotential(), is(equalTo(2)));
    }
}
