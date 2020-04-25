package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellState;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSmoothingTuple {

    @Test
    public void testInitialization() {
        EvacCell e = getEvacCell(1, 1);
        SmoothingTuple s = new SmoothingTuple(e, 0, 10, 0.4);
        
        assertThat(s.getValue(), is(closeTo(10, 10e-6)));
        assertThat(s.getDistanceValue(), is(closeTo(0.4, 10e-6)));
        assertThat(s.getCell(), is(equalTo(e)));
    }
    
    @Test
    public void testMinimumDistance() {
        EvacCell e = getEvacCell(1, 1);
        SmoothingTuple s = new SmoothingTuple(e, 0, 10, Math.sqrt(2) * 0.4);
        
        s.addDistanceParent(0, 0.4);
        
        assertThat(s.getDistanceValue(), is(closeTo(0.4, 10e-6)));
    }
    
    @Test
    public void testMinimumPotential() {
        EvacCell e = getEvacCell(1, 1);
        SmoothingTuple s = new SmoothingTuple(e, 0, 14, 0.4);
        
        s.addParent(1, 10);
        
        assertThat(s.getValue(), is(closeTo(11, 10e-6)));
    }
    
    @Test
    public void testSmoothing() {
        EvacCell e = getEvacCell(1, 1);
        SmoothingTuple s = new SmoothingTuple(e, 4, 10, 0.4);
        
        s.addParent(1, 10);
        assertThat(s.getValue(), is(closeTo(11, 10e-6)));
        s.applySmoothing();
        
        // parent potentials are 5 together, current potential is 11, 2 parents, factor of 3
        assertThat(s.getValue(), is(closeTo(38./5., 10e-6)));
        assertThat(s.getDistanceValue(), is(closeTo(0.4, 10e-6)));
    }
    
    private EvacCell getEvacCell(int x, int y) {
        return new EvacCell(new EvacuationCellState(null), 1, x, y) {
            
            @Override
            public EvacCell clone() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };    }
}
