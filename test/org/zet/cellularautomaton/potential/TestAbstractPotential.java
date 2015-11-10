package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellState;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractPotential {
    private EvacCell getCell() {
        return new EvacCell(new EvacuationCellState(null), 1, 0, 0) {

            @Override
            public void setSpeedFactor(double speedFactor) {
            }

            @Override
            public EvacCell clone() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };        
    }
    
    @Test
    public void initializedPotential() {
        AbstractPotential potential = new AbstractPotential() {
        };
        assertThat(potential.getMaxPotential(), is(equalTo(-1)));
        assertThat(potential.getMappedCells(), is(empty()));
        assertThat(potential.hasValidPotential(getCell()), is(false));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void initializedPotentialGetNonexistent() {
        AbstractPotential potential = new AbstractPotential() {
        };
        potential.getPotential(getCell());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void initializedPotentialGetNonexistentDouble() {
        AbstractPotential potential = new AbstractPotential() {
        };
        potential.getPotentialDouble(getCell());
    }
    
    @Test
    public void testContained() {
        AbstractPotential potential = new AbstractPotential() {
        };
        EvacCell c = getCell();
        potential.setPotential(c, 3);
        assertThat(potential.hasValidPotential(c), is(true));
    }
    
    @Test
    public void setPotentialValue() {
        AbstractPotential potential = new AbstractPotential() {
        };
        EvacCell c = getCell();
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
        AbstractPotential potential = new AbstractPotential() {
        };
        potential.setPotential(null, 3);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteEmptyFails() {
        AbstractPotential potential = new AbstractPotential() {
        };
        potential.deleteCell(getCell());
    }
    
    @Test
    public void testDelete() {
        AbstractPotential potential = new AbstractPotential() {
        };
        EvacCell c = getCell();
        potential.setPotential(c, 3);
        potential.deleteCell(c);
        //assertThat(potential.getPotential(c), is(equalTo(Potential.UNKNOWN_POTENTIAL_VALUE)));
        assertThat(potential.getMappedCells(), is(empty()));
    }
    
    @Test
    public void updateMaxPotentialWhenAdding() {
        AbstractPotential potential = new AbstractPotential() {
        };
        EvacCell c1 = getCell();
        potential.setPotential(c1, 3);
        assertThat(potential.getMaxPotential(), is(equalTo(3)));
        EvacCell c2 = getCell();
        potential.setPotential(c2, 4);
        assertThat(potential.getMaxPotential(), is(equalTo(4)));
        EvacCell c3 = getCell();
        potential.setPotential(c3, 2);
        assertThat(potential.getMaxPotential(), is(equalTo(4)));
    }
    
    @Test
    public void updateMaxPotentialWhenDeleting() {
        AbstractPotential potential = new AbstractPotential() {
        };
        EvacCell c1 = getCell();
        potential.setPotential(c1, 3);
        EvacCell c2 = getCell();
        potential.setPotential(c2, 4);
        EvacCell c3 = getCell();
        potential.setPotential(c3, 2);
        
        potential.deleteCell(c1);
        assertThat(potential.getMaxPotential(), is(equalTo(4)));
        potential.deleteCell(c2);
        assertThat(potential.getMaxPotential(), is(equalTo(2)));
        potential.deleteCell(c3);
        assertThat(potential.getMaxPotential(), is(equalTo(AbstractPotential.UNKNOWN_POTENTIAL_VALUE)));
    }
    
    @Test
    public void updateMaxPotentialWhenResetting() {
        AbstractPotential potential = new AbstractPotential() {
        };
        EvacCell c = getCell();
        potential.setPotential(c, 3);
        potential.setPotential(c, 2);
        assertThat(potential.getMaxPotential(), is(equalTo(2)));
    }
}
